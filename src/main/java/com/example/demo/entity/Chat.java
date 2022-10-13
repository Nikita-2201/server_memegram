package com.example.demo.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String username;


    @Column(columnDefinition = "text", nullable = false)
    private String message;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @ManyToMany()
    @JoinTable(
            name = "user_chats",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                    nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "chats_id", referencedColumnName = "id",
                    nullable = false,updatable = false)})
    private Set<User> users = new HashSet<>();

    @PrePersist
    protected  void onCreate(){this.createDate=LocalDateTime.now();}
}
