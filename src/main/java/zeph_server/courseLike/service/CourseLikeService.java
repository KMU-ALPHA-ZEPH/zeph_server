package zeph_server.courseLike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.repository.CourseRepository;
import zeph_server.course.service.CourseService;
import zeph_server.courseLike.domain.CourseLike;
import zeph_server.courseLike.repository.CourseLikeRepository;
import zeph_server.global.exception.DuplicateException;
import zeph_server.global.exception.NotFoundException;
import zeph_server.user.domain.User;
import zeph_server.user.repository.UserRepository;
import zeph_server.user.service.UserService;

@Service
@RequiredArgsConstructor
public class CourseLikeService {

    private final CourseLikeRepository courseLikeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // 좋아요 추가
    @Transactional
    public void addLike(Long courseId, Long userId) {
        // 1. 중복 체크
        if (courseLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new DuplicateException("이미 좋아요한 코스입니다");
        }

        // 2. User, Course 조회
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("코스를 찾을 수 없습니다"));
        User user = userService.findById(userId);
        // 3. 좋아요 생성
        CourseLike like = CourseLike.builder()
                .user(user)
                .course(course)
                .build();

        courseLikeRepository.save(like);
    }

    // 좋아요 취소
    @Transactional
    public void removeLike(Long courseId, Long userId) {
        CourseLike like = courseLikeRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new NotFoundException("좋아요한 적 없는 코스입니다"));

        courseLikeRepository.delete(like);
    }

    // 좋아요 수 조회
    @Transactional(readOnly = true)
    public long getLikeCount(Long courseId) {
        return courseLikeRepository.countByCourseId(courseId);
    }

    // 본인이 좋아요 눌렀는지 확인
    @Transactional(readOnly = true)
    public boolean isLiked(Long courseId, Long userId) {
        return courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}