package zeph_server.group.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zeph_server.group.domain.Group;
import zeph_server.group.dto.AddGroupRequest;
import zeph_server.group.dto.GroupResponse;
import zeph_server.group.dto.UpdateGroupRequest;
import zeph_server.group.repository.GroupRepository;
import zeph_server.scrap.repository.ScrapRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final ScrapRepository scrapRepository;

    public void addGroup(@Valid AddGroupRequest requestDTO) {
        Group group = Group.builder()
                .name(requestDTO.name())
                .build();
        groupRepository.save(group);
    }

    public List<GroupResponse> getAllGroups(Long userId) {
        List<Group> groups = groupRepository.findAllByUserId(userId);

        return groups.stream()
                .map(group -> GroupResponse.from(
                        group,
                        scrapRepository.countByGroupId(group.getId())
                ))
                .toList();
    }

    public void updateGroup(Long groupId, UpdateGroupRequest requestDTO, Long userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Folder Not Found"));
        if (!group.getUser().getId().equals(userId)) { // 여기도 user 관련 로직 필요
            throw new IllegalArgumentException("권한 없음");
        }
        group.update(requestDTO.name(), requestDTO.description());

    }

    public Group findById(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Folder Not Found"));
    }
}
