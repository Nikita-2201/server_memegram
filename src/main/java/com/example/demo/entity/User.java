package com.example.demo.entity;

import com.example.demo.entity.enums.ERole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data// аннотация для Get/Set
@Entity //
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//айди пользователя
    @Column(nullable = false)
    private String name;//имя
    @Column(unique = true, updatable = false)
    private String username;//ник
    @Column(nullable = false)
    private String lastname;//фамилия
    @Column(unique = true)
    private String email;//почтовый ящик
    @Column(columnDefinition = "text")
    private String bio;//описания пользователя(биография)
    @Column(length = 3000)//3000 для кодирования и декодирования пароля
    private String password;//пароль


    @ElementCollection(targetClass = ERole.class)
    @CollectionTable(name="user_role", joinColumns = @JoinColumn(name="user_id"))// создаем новую таблицу в БД
    private Set<ERole> role = new HashSet<>();// Определение уникальности роли

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,//для получения постов когда сами захотим
    mappedBy = "user", orphanRemoval = true)//cascade нужен для того чтоб когда удаляли пользователя все посты тож уходили из БД
    private List<Post> posts = new ArrayList<>();// Лист постов

    @ManyToMany()
    private Set<Chat> chats = new HashSet<>();


    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdDate;// для отслеживания когда был создан объект

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public User(){

    }

    public User(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities){
        this.id=id;
        this.username=username;
        this.email=email;
        this.password=password;
        this.authorities=authorities;
    }

    @PrePersist// перед созданием БД
    protected void onCreate(){//для отслеживания когда был создан объект
        this.createdDate=LocalDateTime.now();
    }



    /**---
     * Security
     * ---**/
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
