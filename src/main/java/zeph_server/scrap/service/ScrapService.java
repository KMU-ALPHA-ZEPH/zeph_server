package zeph_server.scrap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.course.domain.Course;
import zeph_server.course.service.CourseService;
import zeph_server.global.exception.ForbiddenException;
import zeph_server.global.exception.NotFoundException;
import zeph_server.group.domain.Group;
import zeph_server.group.repository.GroupRepository;
import zeph_server.group.service.GroupService;
import zeph_server.scrap.domain.Scrap;
import zeph_server.scrap.dto.CreateScrapRequest;
import zeph_server.scrap.dto.ScrapPreviewResponse;
import zeph_server.scrap.repository.ScrapRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final CourseService courseService;
    private final GroupService groupService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void createScrap(CreateScrapRequest dto, Long userId) {

        // 1. Course 확보 (신규 생성 or 기존 조회)
        Course course;
        if (dto.courseData() != null) {
            course = courseService.createCourse(dto.courseData());
        } else if (dto.courseId() != null) {
            course = courseService.findById(dto.courseId());
        } else {
            throw new IllegalArgumentException("코스 정보가 필요합니다");
        }

        // 2. 중복 체크
        if (scrapRepository.existsByUserIdAndCourseId(userId, course.getId())) {
            throw new IllegalArgumentException("이미 저장한 코스입니다");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        Group group = null;
        if (dto.groupId() != null) {
            group = groupService.findById(dto.groupId());
            if (!group.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("권한이 없습니다");
            }
        }

        Scrap scrap = Scrap.builder()
                .user(user)
                .course(course)
                .group(group)
                .build();

        scrapRepository.save(scrap);
    }

    @Transactional(readOnly = true)
    public List<ScrapPreviewResponse> getScrapsByGroup(Long userId, Long groupId) {
        // 1. 폴더 존재 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("폴더를 찾을 수 없습니다"));

        // 2. 본인 폴더인지 확인
        if (!group.getUser().getId().equals(userId)) {
            throw new ForbiddenException("본인의 폴더만 조회할 수 있습니다");
        }

        // 3. 스크랩 조회
        List<Scrap> scraps = scrapRepository.findAllByUserIdAndGroupId(userId, groupId);

        return scraps.stream()
                .map(ScrapPreviewResponse::from)
                .toList();
    }
}