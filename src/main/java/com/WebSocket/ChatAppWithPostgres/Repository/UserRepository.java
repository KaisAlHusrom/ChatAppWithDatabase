package com.WebSocket.ChatAppWithPostgres.Repository;

import com.WebSocket.ChatAppWithPostgres.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUserName(String userName);
}
