package com.kg.core.retgister.service;

import com.kg.core.exception.BaseException;
import com.kg.core.retgister.dto.RegisterFormDTO;

/**
 * @author ziro
 * @date 2024/5/27 14:11
 */
public interface RegisterService {
    /**
     * 注册新用户
     *
     * @param regFormDTO
     */
    void add(RegisterFormDTO regFormDTO) throws BaseException;
}
