package com.ajithkumar.ragchatmanagement.service.impl;

import com.ajithkumar.ragchatmanagement.dto.CreateUserRequest;
import com.ajithkumar.ragchatmanagement.entity.User;
import com.ajithkumar.ragchatmanagement.repository.UserRepository;
import com.ajithkumar.ragchatmanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user.
     * @param request create user request
     * @return created user
     */
    public User createUser(CreateUserRequest request) {

        User user = new User(request.getName(), request.getEmail(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        log.info("Creating user with email: {}", request.getEmail());
        User createdUser = userRepository.save(user);
        log.info("User created with ID: {}", createdUser.getId());
        return createdUser;

    }

    // Get User by Email
    public User getUserByEmail(String email) {

        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email).orElse(null));

        if (!user.isPresent()) {
            log.warn("User with email {} does not exist", email);
            throw new IllegalArgumentException("User with email " + email + " does not exist");
        } else {
            return user.get();
        }

    }
}
