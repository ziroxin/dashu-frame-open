package com.kg.core.zuser.mapper;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.core.zuser.dto.ZUserDTO;
import com.kg.core.zuser.dto.ZUserRoleSaveDTO;
import com.kg.core.zuser.entity.ZUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2022-05-01
 */
@Repository
public interface ZUserMapper extends BaseMapper<ZUser> {

    List<ZUserDTO> getUserRoleList(JSONObject paramObj);

    Long getUserRoleCount(JSONObject paramObj);

    ZUserRoleSaveDTO getUserById(@Param("userId") String userId);

    /**
     * 列出子用户
     *
     * @param orgId         组织id
     * @param isIncludeSelf 是否包括自己所在的部门
     * @return {@link List }<{@link ZUser }>
     */
    List<ZUser> listChildrenUsers(@Param("orgId") String orgId,
                                  @Param("isIncludeSelf") boolean isIncludeSelf);
}
