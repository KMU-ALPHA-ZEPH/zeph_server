package zeph_server.group.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.group.dto.AddGroupRequest;
import zeph_server.group.dto.GroupResponse;
import zeph_server.group.dto.UpdateGroupRequest;
import zeph_server.group.service.GroupService;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "groups", description = "그룹 API")
@RestController
@RequestMapping("v0/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<?> addGroup(@RequestBody @Valid AddGroupRequest requestDTO) {
        groupService.addGroup(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups(
    ) {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PatchMapping("/{groupId}")
    public ResponseEntity<?> UpdateGroup(
            @PathVariable Long groupId,
            @RequestBody @Valid UpdateGroupRequest requestDTO
            // 여기에 userId 받아오는 로직 필요
    ) {
        groupService.updateGroup(groupId, requestDTO); // 여기에 userId 추가 필요;
        return ResponseEntity.ok().build();
    }

}
