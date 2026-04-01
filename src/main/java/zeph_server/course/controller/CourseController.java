package zeph_server.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.course.dto.CreateCourseRequest;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.service.CourseService;

@RestController
@RequestMapping("v0/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/recommend")
    public ResponseEntity<?> RecommendCourse(
            @RequestBody RecommendCourseRequest requestDTO
            ){
        courseService.recommendCourse(requestDTO);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createCourse(
            @RequestBody CreateCourseRequest requestDTO
    ){
        courseService.createCourse(requestDTO);
        return ResponseEntity.ok().build();
    }

}
