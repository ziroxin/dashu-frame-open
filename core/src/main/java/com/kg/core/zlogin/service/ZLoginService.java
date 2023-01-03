package com.kg.core.zlogin.service;

import com.kg.core.exception.BaseException;
import com.kg.core.zlogin.dto.LoginFormDTO;
import com.kg.core.zlogin.dto.LoginSuccessDTO;

/**
 * @author ziro
 * @date 2022/5/2 22:14
 */
public interface ZLoginService {
    LoginSuccessDTO login(LoginFormDTO zUser) throws BaseException;

    void logout();
}
