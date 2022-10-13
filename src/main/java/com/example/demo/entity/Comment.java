package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private Long userId;
    @Column(columnDefinition="text", nullable = false)
    private String message;
    private int likes;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    @PrePersist// перед созданием БД
    protected void onCreate(){//для отслеживания когда был создан объект
        this.createdDate= LocalDateTime.now();
    }


}
