package com.ajithkumar.ragchatmanagement.service.impl;

import com.ajithkumar.ragchatmanagement.dto.CreateUserRequest;
import com.ajithkumar.ragchatmanagement.entity.User;
import com.ajithkumar.ragchatmanagement.repository.UserRepository;
import com.ajithkumar.ragchatmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(CreateUserRequest request) {

        User user = new User(request.getName(), request.getEmail(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        User createdUser = userRepository.save(user);
        return createdUser;

    }

    public User getUserByEmail(String email) {

        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email).orElse(null));

        if (!user.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " does not exist");
        } else {
            return user.get();
        }

    }
}
