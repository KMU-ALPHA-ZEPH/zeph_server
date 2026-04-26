package zeph_server.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.course.dto.CreateCourseRequest;
import zeph_server.course.dto.RecommendCourseRequest;
import zeph_server.course.service.CourseService;
import zeph_server.record.dto.request.RunRecordRequestDTO;
import zeph_server.record.dto.response.RunRecordCreateResponseDTO;
import zeph_server.record.service.RunRecordService;

@RestController
@RequestMapping("v0/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final RunRecordService runRecordService;

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

    @PostMapping("/{courseId}/records")
    public ResponseEntity<RunRecordCreateResponseDTO> createRunRecord(
            @PathVariable Long courseId,
            @RequestBody RunRecordRequestDTO requestDTO
    ) {
        RunRecordCreateResponseDTO response =
                runRecordService.saveRunRecord(courseId, requestDTO);

        return ResponseEntity.ok(response);
    }

}
