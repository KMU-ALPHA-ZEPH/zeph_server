package zeph_server.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.course.dto.CourseDetailResponse;
import zeph_server.course.dto.CourseResponse;
import zeph_server.course.dto.CreateCourseRequest;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("v0/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/recommend")
    public ResponseEntity<?> RecommendCourse(
            @RequestBody RecommendCourseRequest requestDTO
    ) {
        courseService.recommendCourse(requestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestBody CreateCourseRequest requestDTO
    ) {
        courseService.createCourse(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(courseService.getAllCourses(region, type));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }
}

