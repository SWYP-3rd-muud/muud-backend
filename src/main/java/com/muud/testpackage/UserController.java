package com.muud.testpackage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserResponseDto>> test() {

        List<UserResponseDto> userList = userService.getUserList();

        return ResponseEntity.ok(userList);
    }
}
