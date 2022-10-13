package com.example.demo.repository;

import com.example.demo.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
    Optional<Chat> findUsernametoChat (String username);
    Optional<Chat> findUserByUserId(Long userId);

}
