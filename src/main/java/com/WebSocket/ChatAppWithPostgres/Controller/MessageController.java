package com.WebSocket.ChatAppWithPostgres.Controller;

import com.WebSocket.ChatAppWithPostgres.Model.Message.Message;
import com.WebSocket.ChatAppWithPostgres.Model.User.User;
import com.WebSocket.ChatAppWithPostgres.Repository.MessageRepository;
import com.WebSocket.ChatAppWithPostgres.Services.MessageService;
import com.WebSocket.ChatAppWithPostgres.Services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    @MessageMapping("/message/{sender_id}/{receiver_id}")
    public Message sendPrivateMessage(
            @Payload Message message,
            @PathVariable Integer sender_id,
            @PathVariable Integer receiver_id
            ) {
        User sender = userService.findUserById(sender_id);
        User receiver = userService.findUserById(receiver_id);

        simpMessagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/private-message",
                message
        );



        return messageService.sendMessage(message, sender, receiver);
    }

}
