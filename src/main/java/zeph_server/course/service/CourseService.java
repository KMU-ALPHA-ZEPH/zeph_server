package zeph_server.course.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.client.AiCourseClient;
import zeph_server.course.domain.Course;
import zeph_server.course.dto.*;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.dto.common.Point;
import zeph_server.course.dto.common.SegmentInfo;
import zeph_server.course.repository.CourseRepository;
import zeph_server.courseLike.service.CourseLikeService;

import zeph_server.global.exception.NotFoundException;
import zeph_server.util.ReverseGeoCalculator;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)


public class CourseService {
    private final ReverseGeoCalculator reverseGeoCalculator;

    private final CourseRepository courseRepository;
    private final CourseLikeService courseLikeService;
    private final AiCourseClient aiCourseClient;
    private final ObjectMapper objectMapper;
    private final GpxWriter gpxWriter;

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

    public List<CourseResponse> getAllCourses(String region, String type, Long userId) {
        List<Course> courses;

        boolean hasRegion = region != null && !region.isBlank();
        boolean hasType = type != null && !type.isBlank();

        if (hasType && hasRegion) {
            courses = courseRepository.findByRegionAndType(region, type);
        } else if (hasType) {
            courses = courseRepository.findByType(type);
        } else if (hasRegion) {
            courses = courseRepository.findByRegion(region);
        } else {
            courses = courseRepository.findAll();
        }

        return courses.stream()
                .map(course -> CourseResponse.create(
                                course,
                                courseLikeService.getLikeCount(course.getId()),
                                courseLikeService.isLiked(course.getId(), userId)
                        )
                )
                .toList();
    }

    public CourseDetailResponse getCourseById(Long id, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 코스를 찾을 수 없습니다."));

        return CourseDetailResponse.create(
                course,
                courseLikeService.getLikeCount(id),
                courseLikeService.isLiked(id, userId)
        );
    }

    public RecommendCourseResponse recommendCourse(RecommendCourseRequest requestDTO) {
//        List<RouteNodeResponse> routeNodes = loadMockRouteNodes();
        AiRecommendResponse aiResponse =
                aiCourseClient.requestRecommendCourse(requestDTO);

        List<RouteNodeResponse> routeNodes = aiResponse.routes();
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
    public Course createCourse(CreateCourseRequest requestDTO) {
        PathData pathData = requestDTO.pathData();
        Point first = pathData.points().get(0);

        Double startLat = first.lat();
        Double startLng = first.lng();

        String region = reverseGeoCalculator.getRegion(startLat, startLng);


        Course course = Course.builder()
                .type(requestDTO.type())
                .name(requestDTO.name())
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
}