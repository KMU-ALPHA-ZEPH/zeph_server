package zeph_server.course.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.dto.*;
import zeph_server.course.dto.common.PathData;
import zeph_server.course.dto.common.Point;
import zeph_server.course.repository.CourseRepository;
import zeph_server.util.ReverseGeoCalculator;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)


public class CourseService {
    private final ReverseGeoCalculator reverseGeoCalculator;

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public List<CourseResponse> getAllCourses(String region, String type) {
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
                .map(this::getCourseDisplay)
                .toList();
    }

    public CourseDetailResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 찾을 수 없습니다."));

        return getCourseDetails(course);
    }

    public void recommendCourse(RecommendCourseRequest requestDTO) {
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


    public CourseResponse getCourseDisplay(Course course) {
        return CourseResponse.create(course);
    }

    public CourseDetailResponse getCourseDetails(Course course) {
        return CourseDetailResponse.create(course);
    }

    public Course findById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("course not found."));
    }
}