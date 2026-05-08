package zeph_server.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "그룹 추가", description = "스크랩에 사용할 폴더 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 생성 성공"),
            @ApiResponse(responseCode = "400", description = "필수 필드 누락 또는 잘못된 형식 (폴더 이름 공백/길이 초과)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 폴더 이름"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<?> addGroup(@RequestBody @Valid AddGroupRequest requestDTO) {
        groupService.addGroup(requestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 그룹 조회", description = "폴더 전체 조회 (내 스크랩 페이지)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 조회 성공 - 데이터 없어도 빈 리스트 보냄"),
            @ApiResponse(responseCode = "400", description = "필수 필드 누락 또는 잘못된 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "DB 조회 과정에서 인덱스 or 연결 오류")
    })
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups(
    ) {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @Operation(summary = "그룹 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 수정 성공"),
            @ApiResponse(responseCode = "400", description = "필수 필드 누락 또는 잘못된 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
