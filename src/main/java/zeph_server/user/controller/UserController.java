package zeph_server.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zeph_server.global.dto.AuthResponseDto;
import zeph_server.user.dto.EmailLoginRequest;
import zeph_server.user.dto.EmailSignupRequest;
import zeph_server.user.dto.UserDto;
import zeph_server.user.dto.UserUpdateDto;
import zeph_server.user.service.UserService;

@Tag(name = "users", description = "사용자 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private final UserService userService;

    @Operation(summary = "이메일 회원가입", description = "이메일, 비밀번호, 이름으로 신규 사용자를 생성한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류 또는 필수 필드 누락"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/users/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody EmailSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "이메일 로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받는다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 형식 오류 또는 필수 필드 누락"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/users/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody EmailLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "카카오 로그인 콜백", description = "카카오 OAuth2 인증 코드 콜백을 처리한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "콜백 처리 성공"),
            @ApiResponse(responseCode = "400", description = "인증 코드 누락 또는 잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<Void> kakaoLoginCallback(
            @Parameter(description = "카카오 OAuth2 인증 코드", required = true)
            @RequestParam("code") String code) {
        System.out.println("controller");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 프로필 조회", description = "사용자 ID로 프로필 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getProfile(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getProfile(id)); //200
    }

    @Operation(summary = "사용자 프로필 수정", description = "사용자 이름과 프로필 이미지를 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateProfile(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable("id") Long id,
            @RequestBody UserUpdateDto dto) {
        userService.updateProfile(id, dto);
        return ResponseEntity.noContent().build(); //204
    }

    @Operation(summary = "사용자 삭제", description = "사용자 ID로 계정을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteProfile(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable("id") Long id) {
        userService.deleteProfile(id);
        return ResponseEntity.noContent().build(); //204
    }
}
