package zeph_server.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zeph_server.user.dto.UserUpdateDto;
import zeph_server.user.dto.UserDto;
import zeph_server.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private final UserService userService;

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<Void> kakaoLoginCallback(@RequestParam String code) {
        System.out.println("controller");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getProfile(@PathVariable Long id){
        return ResponseEntity.ok(userService.getProfile(id)); //200
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateProfile(@PathVariable Long id, @RequestBody UserUpdateDto dto){
        userService.updateProfile(id, dto);
        return ResponseEntity.noContent().build(); //204
    }
}