package zeph_server.course.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.client.AiCourseClient;
import zeph_server.course.domain.Course;
import zeph_server.course.domain.CourseSortType;
import zeph_server.course.dto.*;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.dto.common.Point;
import zeph_server.course.dto.common.SegmentInfo;
import zeph_server.course.repository.CourseRepository;
import zeph_server.courseLike.service.CourseLikeService;
import zeph_server.scrap.domain.Scrap;
import zeph_server.scrap.repository.ScrapRepository;

import zeph_server.global.exception.CustomException;
import zeph_server.global.exception.DuplicateException;
import zeph_server.global.exception.GlobalErrorCode;
import zeph_server.global.exception.NotFoundException;
import zeph_server.user.domain.User;
import zeph_server.user.service.UserService;
import zeph_server.util.GeoUtils;
import zeph_server.util.ReverseGeoCalculator;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)


public class CourseService {
    private final ReverseGeoCalculator reverseGeoCalculator;

    private final CourseRepository courseRepository;
    private final CourseLikeService courseLikeService;
    private final ScrapRepository scrapRepository;
    private final AiCourseClient aiCourseClient;
    private final ObjectMapper objectMapper;
    private final GpxWriter gpxWriter;
    private final UserService userService;

    private List<RouteNodeResponse> loadMockRouteNodes() {
        try {
            ClassPathResource resource = new ClassPathResource("routes_output.json");

            return objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<>() {
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException("추천 경로 mock JSON 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    public List<CourseResponse> getAllCourses(CourseSearchCondition condition, Long userId) {
        condition.validate();

        List<Course> courses;
        boolean hasRegion = condition.hasRegion();
        boolean hasType = condition.hasType();
        String region = condition.region();
        String type = condition.type();

        if (condition.isLiked()) {
            // 좋아요한 코스만 대상으로 하고, region/type 필터는 인메모리로 적용
            courses = courseLikeService.getLikedCourses(userId);
            if (hasRegion) {
                courses = courses.stream().filter(c -> region.equals(c.getRegion())).toList();
            }
            if (hasType) {
                courses = courses.stream().filter(c -> type.equals(c.getType())).toList();
            }
        } else if (hasType && hasRegion) {
            courses = courseRepository.findByRegionAndType(region, type);
        } else if (hasType) {
            courses = courseRepository.findByType(type);
        } else if (hasRegion) {
            courses = courseRepository.findByRegion(region);
        } else {
            courses = courseRepository.findAll();
        }

        // 반경 필터: 기준 위치(lat/lng)로부터 radiusKm 이내인 코스만 남김 (시작점 기준 직선거리)
        Double radiusKm = condition.radiusKm();
        Double lat = condition.lat();
        Double lng = condition.lng();
        if (radiusKm != null) {
            courses = courses.stream()
                    .filter(c -> distanceFromUser(c, lat, lng) <= radiusKm)
                    .toList();
        }

        // 코스 경로 길이(distanceKm) 범위 필터
        Double minDistanceKm = condition.minDistanceKm();
        Double maxDistanceKm = condition.maxDistanceKm();
        if (minDistanceKm != null) {
            courses = courses.stream()
                    .filter(c -> c.getDistanceKm() >= minDistanceKm)
                    .toList();
        }
        if (maxDistanceKm != null) {
            courses = courses.stream()
                    .filter(c -> c.getDistanceKm() <= maxDistanceKm)
                    .toList();
        }

        Map<Long, Long> likeCountMap = courses.stream()
                .collect(Collectors.toMap(
                        Course::getId,
                        course -> courseLikeService.getLikeCount(course.getId())
                ));

        // 인기 경로 탭: 전역 좋아요 1개 이상 받은 코스만 대상
        // (liked 폴더는 내가 좋아요한 코스라 이미 ≥1이므로 제외)
        if (!condition.isLiked()) {
            courses = courses.stream()
                    .filter(c -> likeCountMap.getOrDefault(c.getId(), 0L) >= 1)
                    .toList();
        }

        // liked 목록은 sort 미지정 시 쿼리의 최신 좋아요순(likedAt desc)을 유지
        // 그 외 목록은 기존대로 인기순(POPULAR)을 기본 정렬로 적용
        List<Course> sorted = (condition.isLiked() && !condition.hasSort())
                ? courses
                : courses.stream()
                        .sorted(buildComparator(CourseSortType.from(condition.sort()), likeCountMap, lat, lng))
                        .toList();

        // 현재 유저의 스크랩을 한 번에 조회해 courseId → scrapId 매핑 (N+1 방지)
        List<Long> courseIds = sorted.stream().map(Course::getId).toList();
        Map<Long, Long> scrapIdMap = scrapRepository.findAllByUserIdAndCourseIdIn(userId, courseIds).stream()
                .collect(Collectors.toMap(scrap -> scrap.getCourse().getId(), Scrap::getId));

        return sorted.stream()
                .map(course -> CourseResponse.create(
                                course,
                                likeCountMap.get(course.getId()),
                                courseLikeService.isLiked(course.getId(), userId),
                                scrapIdMap.get(course.getId())
                        )
                )
                .toList();
    }

    private Comparator<Course> buildComparator(CourseSortType sortType,
                                               Map<Long, Long> likeCountMap,
                                               Double lat, Double lng) {
        return switch (sortType) {
            case DISTANCE_ASC -> Comparator.comparing(Course::getDistanceKm);
            case DISTANCE_DESC -> Comparator.comparing(Course::getDistanceKm).reversed();
            case POPULAR -> Comparator
                    .comparingLong((Course course) -> likeCountMap.getOrDefault(course.getId(), 0L))
                    .reversed();
            case NEAREST -> {
                if (lat == null || lng == null) {
                    throw new CustomException(GlobalErrorCode.INVALID_REQUEST);
                }
                yield Comparator.comparingDouble(course -> distanceFromUser(course, lat, lng));
            }
        };
    }

    // 코스 시작점과 사용자 설정 위치 사이의 직선 거리
    // 경로 데이터가 없으면 정렬 시 맨 뒤로
    private double distanceFromUser(Course course, double lat, double lng) {
        PathData pathData = course.getPathData();
        if (pathData == null || pathData.points() == null || pathData.points().isEmpty()) {
            return Double.MAX_VALUE;
        }
        Point start = pathData.points().get(0);
        if (start.lat() == null || start.lng() == null) {
            return Double.MAX_VALUE;
        }
        return GeoUtils.haversineKm(lat, lng, start.lat(), start.lng());
    }

    public CourseDetailResponse getCourseById(Long id, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 코스를 찾을 수 없습니다."));

        Long scrapId = scrapRepository.findByUserIdAndCourseId(userId, id)
                .map(Scrap::getId)
                .orElse(null);

        return CourseDetailResponse.create(
                course,
                courseLikeService.getLikeCount(id),
                courseLikeService.isLiked(id, userId),
                scrapId
        );
    }

    public RecommendCourseResponse recommendCourse(RecommendCourseRequest requestDTO) {
//        List<RouteNodeResponse> routeNodes = loadMockRouteNodes();
        AiRecommendResponse aiResponse =
                aiCourseClient.requestRecommendCourse(requestDTO);

        RecommendedRouteResponse route =
                aiResponse.routes().get(0);

        List<RouteNodeResponse> routeNodes =
                route.points();

        if (routeNodes == null || routeNodes.isEmpty()) {

            throw new IllegalStateException("AI가 추천 경로를 반환하지 않았습니다.");

        }
        List<Point> points = routeNodes.stream()
                .map(node -> new Point(
                        node.id(),
                        node.lat(),
                        node.lng(),
                        node.segmentToNext() == null ? null :
                                new SegmentInfo(
                                        node.segmentToNext().lengthM(),
                                        node.segmentToNext().avgBrightness(),
                                        node.segmentToNext().slopeType(),
                                        node.segmentToNext().nearPark(),
                                        node.segmentToNext().trafficlightCount(),
                                        node.segmentToNext().trafficVolumeScore()
                                )
                ))
                .toList();

        PathData pathData = new PathData(points);

        double totalDistanceM = routeNodes.stream()
                .filter(node -> node.segmentToNext() != null)
                .filter(node -> node.segmentToNext().lengthM() != null)
                .mapToDouble(node -> node.segmentToNext().lengthM())
                .sum();

        return new RecommendCourseResponse(
                requestDTO.distanceKm(),
                totalDistanceM / 1000.0,
                requestDTO.type(),
                requestDTO.roundTrip(),
                requestDTO.startLat(),
                requestDTO.startLng(),
                pathData,
                aiResponse.preferenceSummary()
        );
    }


    @Transactional
    public Course createCourse(CreateCourseRequest requestDTO, Long userId) {
        PathData pathData = requestDTO.pathData();
        Point first = pathData.points().get(0);

        Double startLat = first.lat();
        Double startLng = first.lng();

        String region = reverseGeoCalculator.getRegion(startLat, startLng);

        User user = userService.findById(userId);

        Course course = Course.builder()
                .user(user)
                .name(requestDTO.name())
                .type(requestDTO.type())
                .description(requestDTO.description())
                .distanceKm(requestDTO.distanceKm())
                .roundTrip(requestDTO.roundTrip())
                .pathData(requestDTO.pathData())
                .region(region)
                .preferLighting(requestDTO.preferLighting())          // 추가
                .preferConvenience(requestDTO.preferConvenience())    // 추가
                .slopePreference(requestDTO.slopePreference())
                .build();
        return courseRepository.save(course);
    }


    public Course findById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("course not found."));
    }

    public String exportCourseGpx(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("해당 코스를 찾을 수 없습니다."));

        PathData pathData = course.getPathData();
        if (pathData == null || pathData.points() == null || pathData.points().isEmpty()) {
            throw new NotFoundException("해당 코스에 경로 데이터가 없습니다.");
        }

        return gpxWriter.writeRoute(course.getName(), pathData.points());
    }

    @Transactional
    public void updateCourse(Long courseId, @Valid UpdateCourseRequest requestDTO, Long userId) {
        String newName = requestDTO.name().trim();
        Course course = courseRepository
                .findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new NotFoundException("Course Not Found"));

        // 수정 대상 코스 자신은 제외하고, 다른 코스가 같은 이름을 쓸 때만 중복 처리
        if (courseRepository.existsByUserIdAndNameAndIdNot(userId, newName, courseId)) {
            throw new DuplicateException("이미 존재하는 코스 이름입니다");
        }

        course.update(
                newName,
                requestDTO.description()
        );

    }
}