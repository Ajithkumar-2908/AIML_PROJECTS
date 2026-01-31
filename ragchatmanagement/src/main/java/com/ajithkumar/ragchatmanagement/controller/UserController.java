package com.ajithkumar.ragchatmanagement.controller;

import com.ajithkumar.ragchatmanagement.dto.CreateChatSessionRequest;
import com.ajithkumar.ragchatmanagement.dto.CreateUserRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.entity.User;
import com.ajithkumar.ragchatmanagement.service.UserService;
import com.ajithkumar.ragchatmanagement.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request) {


        User response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {

        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);

    }
}
