package com.ajithkumar.ragchatmanagement.service;

import com.ajithkumar.ragchatmanagement.dto.CreateUserRequest;
import com.ajithkumar.ragchatmanagement.entity.User;

public interface UserService {

    User createUser(CreateUserRequest request);

    User getUserByEmail(String email);

}
