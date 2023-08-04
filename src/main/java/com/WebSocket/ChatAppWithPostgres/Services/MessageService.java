package com.WebSocket.ChatAppWithPostgres.Services;

import com.WebSocket.ChatAppWithPostgres.Model.Message.Message;
import com.WebSocket.ChatAppWithPostgres.Model.User.User;
import com.WebSocket.ChatAppWithPostgres.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    //Send Message
    public Message sendMessage(Message message, User sender, User receiver) {
        message.setSender(sender);
        message.setReceiver(receiver);

        sender.getSentMessages().add(message);
        receiver.getReceivedMessages().add(message);

        return messageRepository.save(message);
    }
}
