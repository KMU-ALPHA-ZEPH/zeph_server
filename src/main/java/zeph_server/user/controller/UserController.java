package zeph_server.user.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody EmailSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/users/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody EmailLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<Void> kakaoLoginCallback(@RequestParam("code") String code) {
        System.out.println("controller");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getProfile(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.getProfile(id)); //200
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateProfile(@PathVariable("id") Long id, @RequestBody UserUpdateDto dto){
        userService.updateProfile(id, dto);
        return ResponseEntity.noContent().build(); //204
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable("id") Long id){
        userService.deleteProfile(id);
        return ResponseEntity.noContent().build(); //204
    }
}
