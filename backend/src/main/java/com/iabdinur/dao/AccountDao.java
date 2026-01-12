package com.iabdinur.dao;

import com.iabdinur.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDao {
    List<Account> selectAllAccounts();
    Optional<Account> selectAccountById(Long accountId);
    void insertAccount(Account account);
    boolean existsAccountWithUsername(String username);
    boolean existsAccountById(Long accountId);
    void deleteAccountById(Long accountId);
    void updateAccount(Account update);
    Optional<Account> selectAccountByUsername(String username);
}
