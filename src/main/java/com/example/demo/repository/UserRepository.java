package com.example.demo.repository;
//класс-интерфейс отвечающий за хранилище
import com.example.demo.entity.Chat;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {//1 тип объекта с БД который хотим получить, 2 тип id
    //select * from Users as u where username='username';
    Optional<User> findUserByUsername(String username);//Optional для проверки существования объекта в БД(является оберткой)
    Optional<User> findUserByLastname(String lastname);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(Long id);



}
