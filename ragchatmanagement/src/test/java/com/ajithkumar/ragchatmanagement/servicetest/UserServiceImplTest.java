package com.ajithkumar.ragchatmanagement.servicetest;

import com.ajithkumar.ragchatmanagement.dto.CreateUserRequest;
import com.ajithkumar.ragchatmanagement.entity.User;
import com.ajithkumar.ragchatmanagement.repository.UserRepository;
import com.ajithkumar.ragchatmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserRequest request;
    private User mockUser;

    @BeforeEach
    void setUp() {
        request = new CreateUserRequest();
        request.setName("Ajithkumar");
        request.setEmail("ajithkumar@example.com");

        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setName("Ajithkumar");
        mockUser.setEmail("ajithkumar@example.com");
    }

    @Test
    @DisplayName("createUser - success path")
    void createUser_Success() {
        // Pre-requisites
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        // Invoke
        User createdUser = userService.createUser(request);

        // Assert
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Ajithkumar");
        assertThat(createdUser.getEmail()).isEqualTo("ajithkumar@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getUserByEmail - returns user when exists")
    void getUserByEmail_UserExists() {
        // Pre-requisites
        when(userRepository.findByEmail("ajithkumar@example.com")).thenReturn(Optional.of(mockUser));

        // Invoke
        User user = userService.getUserByEmail("ajithkumar@example.com");

        // Assert
        assertThat(user).isEqualTo(mockUser);
        verify(userRepository).findByEmail("ajithkumar@example.com");
    }

    @Test
    @DisplayName("getUserByEmail - throws exception when user not found")
    void getUserByEmail_UserNotFound() {
        // Pre-requisites
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Invoke & Assert
        assertThatThrownBy(() -> userService.getUserByEmail("unknown@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email unknown@example.com does not exist");
    }
}

