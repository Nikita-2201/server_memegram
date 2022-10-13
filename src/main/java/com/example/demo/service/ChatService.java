package com.example.demo.service;

import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private ChatRepository chatRepository;
    private UserRepository userRepository;
}
