package com.iabdinur.dao;

import com.iabdinur.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> selectAllUsers();
    Optional<User> selectUserById(Long userId);
    void insertUser(User user);
    boolean existsUserWithEmail(String email);
    boolean existsUserById(Long userId);
    void deleteUserById(Long userId);
    void updateUser(User update);
    Optional<User> selectUserByEmail(String email);
}
