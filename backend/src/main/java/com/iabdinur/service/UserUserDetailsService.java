package com.iabdinur.service;

import com.iabdinur.dao.UserDao;
import com.iabdinur.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public UserUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.selectUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email [%s] not found".formatted(email)));

        return user;
    }
}
