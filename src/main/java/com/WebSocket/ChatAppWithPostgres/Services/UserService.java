package com.WebSocket.ChatAppWithPostgres.Services;

import com.WebSocket.ChatAppWithPostgres.Model.User.User;
import com.WebSocket.ChatAppWithPostgres.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;

    //Get All users Service
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User findUserById(Integer id) {
        return userRepo.findById(id).orElseThrow();
    }
}
