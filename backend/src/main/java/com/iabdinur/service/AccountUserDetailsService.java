package com.iabdinur.service;

import com.iabdinur.dao.AccountDao;
import com.iabdinur.model.Account;
import com.iabdinur.model.AccountUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountDao accountDao;

    public AccountUserDetailsService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountDao.selectAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account with username [%s] not found".formatted(username)));

        return new AccountUserDetails(account);
    }
}
