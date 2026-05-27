package zeph_server.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeph_server.global.exception.DuplicateException;
import zeph_server.global.exception.ForbiddenException;
import zeph_server.global.exception.NotFoundException;
import zeph_server.group.domain.Group;
import zeph_server.group.dto.AddGroupRequest;
import zeph_server.group.dto.GroupResponse;
import zeph_server.group.dto.UpdateGroupRequest;
import zeph_server.group.repository.GroupRepository;
import zeph_server.scrap.repository.ScrapRepository;
import zeph_server.user.domain.User;
import zeph_server.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addGroup(AddGroupRequest requestDTO, Long userId) {
        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));

        // 중복 체크
        if (groupRepository.existsByUserIdAndName(userId, requestDTO.name())) {
            throw new DuplicateException("이미 존재하는 폴더 이름입니다");
        }

        // Group 생성
        Group group = Group.builder()
                .name(requestDTO.name())
                .user(user)         // ← 추가
                .build();

        groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("유저를 찾을 수 없습니다");
        }
        List<Group> groups = groupRepository.findAllByUserId(userId);

        return groups.stream()
                .map(group -> GroupResponse.from(
                        group,
                        scrapRepository.countByGroupId(group.getId())
                ))
                .toList();
    }

    @Transactional
    public void updateGroup(Long groupId, UpdateGroupRequest requestDTO, Long userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Folder Not Found"));
        if (!group.getUser().getId().equals(userId)) { // 여기도 user 관련 로직 필요
            throw new ForbiddenException("권한 없음");
        }
        if (!group.getName().equals(requestDTO.name())  // 이름이 바뀌는 경우만 체크
                && groupRepository.existsByUserIdAndName(userId, requestDTO.name())) {
            throw new DuplicateException("이미 존재하는 폴더 이름입니다");
        }

        group.update(requestDTO.name(), requestDTO.description());

    }

    public Group findById(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("폴더를 찾을 수 없습니다"));
    }
}
