package com.WebSocket.ChatAppWithPostgres.Repository;

import com.WebSocket.ChatAppWithPostgres.Model.Message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
