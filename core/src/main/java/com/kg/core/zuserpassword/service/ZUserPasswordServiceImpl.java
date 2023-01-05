package com.kg.core.zuserpassword.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.core.zuserpassword.entity.ZUserPassword;
import com.kg.core.zuserpassword.mapper.ZUserPasswordMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户密码修改记录 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-01-04
 */
@Service
public class ZUserPasswordServiceImpl extends ServiceImpl<ZUserPasswordMapper, ZUserPassword> implements ZUserPasswordService {
}
