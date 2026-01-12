package com.iabdinur.mapper;

import com.iabdinur.dto.AccountDTO;
import com.iabdinur.model.Account;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AccountDTOMapper implements Function<Account, AccountDTO> {

    @Override
    public AccountDTO apply(Account account) {
        return AccountDTO.fromEntity(account);
    }
}
