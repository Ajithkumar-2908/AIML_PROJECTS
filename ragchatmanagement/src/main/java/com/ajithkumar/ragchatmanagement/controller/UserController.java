package com.ajithkumar.ragchatmanagement.controller;

import com.ajithkumar.ragchatmanagement.dto.CreateChatSessionRequest;
import com.ajithkumar.ragchatmanagement.dto.CreateUserRequest;
import com.ajithkumar.ragchatmanagement.entity.ChatSession;
import com.ajithkumar.ragchatmanagement.entity.User;
import com.ajithkumar.ragchatmanagement.service.UserService;
import com.ajithkumar.ragchatmanagement.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Create a new user.
     *
     * @param request the create user request
     * @return response entity with created user
     */
    @PostMapping()
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("Received request to create user: {}", request.getEmail());
        User response = userService.createUser(request);

        log.info("User created successfully: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    /**
     * Get user by email.
     *
     * @param email the user email
     * @return response entity with user details
     */
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {

        log.info("Received request to get user by email: {}", email);
        User user = userService.getUserByEmail(email);
        log.info("User retrieved successfully: {}", user.getId());
        return ResponseEntity.ok(user);

    }
}
