package com.sing4u.kr.user.dto.request;

import com.sing4u.kr.user.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateAccountTypeRequest {
    private AccountType accountType;
}
