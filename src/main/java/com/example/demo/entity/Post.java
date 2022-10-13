package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String caption;
    private String location;
    private int likes;

    @Column
    @ElementCollection(targetClass = String.class)
    private Set<String> likedUsers = new HashSet<>();//пользователи которые лайкнули пост

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;//пользователь который создал данный пост

    @OneToMany(cascade=CascadeType.REFRESH, fetch = FetchType.EAGER, mappedBy="post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();//Создания листа с комментами(У поста может быть множество коммент.)


    @Column(updatable = false)
    private LocalDateTime createdDate;

    @PrePersist// перед созданием БД
    protected void onCreate(){//для отслеживания когда был создан объект
        this.createdDate=LocalDateTime.now();
    }


}
