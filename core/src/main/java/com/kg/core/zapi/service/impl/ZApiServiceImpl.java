package com.kg.core.zapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.zapi.dto.ApiUserIdDTO;
import com.kg.core.zapi.dto.ZApiClassDTO;
import com.kg.core.zapi.dto.ZApiDTO;
import com.kg.core.zapi.entity.ZApi;
import com.kg.core.zapi.mapper.ZApiMapper;
import com.kg.core.zapi.service.IZApiService;
import com.kg.core.zapigroup.entity.ZApiGroup;
import com.kg.core.zapigroup.service.IZApiGroupService;
import com.kg.core.zuser.entity.ZUserRole;
import com.kg.core.zuser.service.IZUserRoleService;
import io.swagger.annotations.ApiOperation;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * API信息表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-05-05
 */
@Service
public class ZApiServiceImpl extends ServiceImpl<ZApiMapper, ZApi> implements IZApiService {

    @Autowired
    private ZApiMapper zApiMapper;
    @Autowired
    private IZApiGroupService apiGroupService;
    @Autowired
    private IZUserRoleService userRoleService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<String> listApiByUserId(String userId) {
        if (LoginConstant.isDeveloper(userId)) {
            // 判断是否开发管理员，拥有全部api权限（直接返回全部权限标记）
            return scanApiList().stream().map(zApi -> zApi.getApiPermission()).collect(Collectors.toList());
        }
        // 角色id和权限标记的关系
        List<ApiUserIdDTO> list;
        if (redisUtils.hasKey(CacheConstant.ROLE_API_REDIS_KEY)) {
            list = (List<ApiUserIdDTO>) redisUtils.get(CacheConstant.ROLE_API_REDIS_KEY);
        } else {
            list = zApiMapper.listAllApiForUserId();
            redisUtils.setNoTimeLimit(CacheConstant.ROLE_API_REDIS_KEY, list);
        }
        // 查出用户角色
        QueryWrapper<ZUserRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ZUserRole::getUserId, userId);
        List<ZUserRole> userRoleList = userRoleService.list(wrapper);
        List<String> roles = userRoleList.stream().map(zUserRole -> zUserRole.getRoleId()).collect(Collectors.toList());
        // 根据其角色，过滤该用户的权限标记
        return list.stream().filter(obj -> roles.contains(obj.getRoleId()))
                .map(obj -> obj.getApiPermission()).collect(Collectors.toList());
    }

    @Override
    public void saveScanApi() {
        // 查询数据库中已有接口
        List<ZApi> list = list();
        List<ZApi> scanList = scanApiList();
        // 在扫描到的apiList中，排除已存在的
        List<ZApi> noList = scanList.stream()
                .filter(zApi -> {
                    Optional<ZApi> result = list.stream().filter(api -> api.getApiPermission().equals(zApi.getApiPermission())).findAny();
                    return result.isPresent() ? false : true;
                })
                .collect(Collectors.toList());
        // 不存在的apiList，准备添加（设置id等）
        List<ZApi> saveList = noList.stream().map(zApi -> {
            zApi.setApiId(GuidUtils.getUuid());
            Optional<Integer> maxOrder = list.stream()
                    .filter(zApi1 -> zApi1.getApiOrder() != null)
                    .map(zApi1 -> zApi1.getApiOrder())
                    .distinct().sorted((o1, o2) -> o2 - o1).findFirst();
            zApi.setApiOrder(maxOrder.isPresent() ? maxOrder.get() + 1 : 1);
            zApi.setCreateTime(LocalDateTime.now());
            return zApi;
        }).collect(Collectors.toList());
        // 保存不存在的
        saveBatch(saveList);
        // 更新已存在的数据
        List<ZApi> updateList = scanList.stream()
                .filter(zApi -> {
                    Optional<ZApi> result = list.stream().filter(api -> api.getApiPermission().equals(zApi.getApiPermission())).findAny();
                    return result.isPresent() ? true : false;
                })
                .map(zApi -> {
                    Optional<ZApi> result = list.stream().filter(api -> api.getApiPermission().equals(zApi.getApiPermission())).findFirst();
                    zApi.setApiId(result.get().getApiId());
                    zApi.setUpdateTime(LocalDateTime.now());
                    return zApi;
                })
                .collect(Collectors.toList());
        updateBatchById(updateList);
    }

    @Override
    public void clearApi() {
        // 查询数据库中已有接口
        List<ZApi> list = list();
        List<ZApi> scanList = scanApiList();
        // 数据库中有，扫描列表中没有的
        List<ZApi> noList = list.stream()
                .filter(zApi -> {
                    Optional<ZApi> result = scanList.stream().filter(api -> api.getApiPermission().equals(zApi.getApiPermission())).findAny();
                    return result.isPresent() ? false : true;
                })
                .collect(Collectors.toList());
        removeBatchByIds(noList);
    }

    /**
     * 得到zApi列表
     */
    @Override
    public List<ZApi> getZApiList() {
        return scanApiList();
    }

    @Override
    public List<ZApiDTO> listGroupApi() {
        List<ZApiDTO> result = new ArrayList<>();
        // 查询所有list
        QueryWrapper<ZApi> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByAsc(ZApi::getApiGroupId).orderByAsc(ZApi::getApiOrder);
        List<ZApi> apiList = list(wrapper);
        // 查询分组
        QueryWrapper<ZApiGroup> orderWrapper = new QueryWrapper<>();
        orderWrapper.lambda().orderByAsc(ZApiGroup::getGroupOrder);
        List<ZApiGroup> listGroup = apiGroupService.list(orderWrapper);
        if (listGroup != null && listGroup.size() > 0) {
            for (ZApiGroup group : listGroup) {
                List<ZApi> collect = apiList.stream()
                        .filter(api -> api.getApiGroupId() != null && api.getApiGroupId().equals(group.getApiGroupId()))
                        .collect(Collectors.toList());
                ZApiDTO apiDTO = new ZApiDTO();
                apiDTO.setApiGroupId(group.getApiGroupId());
                apiDTO.setGroupName(group.getGroupName());
                // 查询class
                List<String> classList = collect.stream().map(zApi -> zApi.getApiClassName())
                        .filter(str -> str != null).distinct().collect(Collectors.toList());
                List<ZApiClassDTO> apiClassList = new ArrayList<>();
                // 遍历class，组合api
                for (String cls : classList) {
                    ZApiClassDTO apiClassDTO = new ZApiClassDTO();
                    apiClassDTO.setClassName(cls);
                    apiClassDTO.setApiList(collect.stream()
                            .filter(api -> api.getApiClassName() != null && api.getApiClassName().equals(cls)).collect(Collectors.toList()));
                    apiClassList.add(apiClassDTO);
                }
                apiDTO.setApiClass(apiClassList);
                result.add(apiDTO);
            }
        }
        ZApiDTO apiDTO = new ZApiDTO();
        apiDTO.setApiGroupId("no_group_api");
        apiDTO.setGroupName("未分组");
        // 查询class
        List<String> classList = apiList.stream()
                .filter(api -> api.getApiGroupId() == null || "".equals(api.getApiGroupId()))//过滤无分组的list
                .map(zApi -> zApi.getApiClassName())
                .filter(str -> str != null).distinct().collect(Collectors.toList());
        List<ZApiClassDTO> apiClassList = new ArrayList<>();
        // 遍历class，组合api
        for (String cls : classList) {
            ZApiClassDTO apiClassDTO = new ZApiClassDTO();
            apiClassDTO.setClassName(cls);
            apiClassDTO.setApiList(apiList.stream()
                    .filter(api -> api.getApiGroupId() == null || "".equals(api.getApiGroupId()))//过滤无分组的list
                    .filter(api -> api.getApiClassName() != null && api.getApiClassName().equals(cls))
                    .collect(Collectors.toList()));
            apiClassList.add(apiClassDTO);
        }
        apiDTO.setApiClass(apiClassList);
        result.add(0, apiDTO);

        return result;
    }

    /**
     * 扫描所有Api列表
     */
    private List<ZApi> scanApiList() {
        // 需要扫描的包路径
        String scanPackage = "com.kg";
        List<ZApi> zApiList = new ArrayList<>();
        //设置扫描路径
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(scanPackage))
                .setScanners(new MethodAnnotationsScanner()));
        //扫描包内带有@RequiresPermissions注解的所有方法集合
        Set<Method> methods = reflections.getMethodsAnnotatedWith(PreAuthorize.class);

        methods.forEach(method -> {
            PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
            ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);

            // 组装实体
            ZApi zApi = new ZApi();
            zApi.setApiClassName(method.getDeclaringClass().getSimpleName());
            zApi.setApiMethodName(method.getName());
            //hasAuthority('permission:delete')
            zApi.setApiPermission(annotation.value().replace("hasAuthority('", "").replace("')", ""));
            if (apiOperation != null) {
                zApi.setApiName(apiOperation.notes());
                zApi.setApiRequestUrl(apiOperation.value());
                zApi.setApiRequestMethod(apiOperation.httpMethod());
                zApi.setApiDescription(apiOperation.notes());
            }
            zApiList.add(zApi);
        });
        return zApiList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deletApiGroup(String apiGroupId) {
        // 清空api表内分组
        QueryWrapper<ZApi> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ZApi::getApiGroupId, apiGroupId);
        List<ZApi> apiList = list(wrapper);
        List<ZApi> updateList = apiList.stream().map(zpi -> {
            zpi.setApiGroupId("");
            return zpi;
        }).collect(Collectors.toList());
        updateBatchById(updateList);
        // 删除分组
        apiGroupService.removeById(apiGroupId);
        return true;
    }
}
