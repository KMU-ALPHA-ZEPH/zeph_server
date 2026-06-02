package zeph_server.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zeph_server.global.exception.DuplicateException;
import zeph_server.global.exception.ForbiddenException;
import zeph_server.global.exception.NotFoundException;
import zeph_server.global.s3.S3ImageService;
import zeph_server.group.domain.Group;
import zeph_server.group.dto.AddGroupRequest;
import zeph_server.group.dto.GroupResponse;
import zeph_server.group.dto.UpdateGroupRequest;
import zeph_server.group.repository.GroupRepository;
import zeph_server.scrap.domain.Scrap;
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
    private final S3ImageService s3ImageService;

    @Value("${aws.s3.default-group-image-key:defaults/group.png}")
    private String defaultGroupImageKey;

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
                .description(requestDTO.description())
                .user(user)
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
                        scrapRepository.countByGroupId(group.getId()),
                        groupImageUrl(group)
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

    @Transactional
    public void updateGroup(Long groupId, String name, String description, MultipartFile image, Long userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Folder Not Found"));
        if (!group.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한 없음");
        }
        String nextName = name == null || name.isBlank() ? group.getName() : name;
        String nextDescription = description == null ? group.getDescription() : description;

        if (!group.getName().equals(nextName)
                && groupRepository.existsByUserIdAndName(userId, nextName)) {
            throw new DuplicateException("이미 존재하는 폴더 이름입니다");
        }

        group.update(nextName, nextDescription);
        if (image != null && !image.isEmpty()) {
            group.updateImage(s3ImageService.uploadGroupImage(groupId, image));
        }

    }

    public Group findById(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("폴더를 찾을 수 없습니다"));
    }

    private String groupImageUrl(Group group) {
        if (group.getImageKey() != null && !group.getImageKey().isBlank()) {
            return s3ImageService.toPublicUrl(group.getImageKey());
        }
        return s3ImageService.toPublicUrl(defaultGroupImageKey);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Folder Not Found"));

        if (!group.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한 없음");
        }

        scrapRepository.clearGroupByGroupId(groupId);
        groupRepository.delete(group);
    }
}
