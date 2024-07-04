package com.kg.module.message.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.entity.ZUserRole;
import com.kg.core.zuser.service.IZUserRoleService;
import com.kg.core.zuser.service.IZUserService;
import com.kg.module.message.dto.MessageCountsDTO;
import com.kg.module.message.dto.ZMessageDTO;
import com.kg.module.message.dto.convert.ZMessageConvert;
import com.kg.module.message.entity.ZMessage;
import com.kg.module.message.excels.ZMessageExcelConstant;
import com.kg.module.message.excels.ZMessageExcelOutDTO;
import com.kg.module.message.mapper.ZMessageMapper;
import com.kg.module.message.dto.MessageToBaseDTO;
import com.kg.module.messageTo.entity.ZMessageTo;
import com.kg.module.messageTo.service.ZMessageToService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息中心 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@Service
public class ZMessageServiceImpl extends ServiceImpl<ZMessageMapper, ZMessage> implements ZMessageService {

    @Resource
    private ZMessageConvert zMessageConvert;
    @Resource
    private ZMessageMapper mapper;
    @Resource
    private ZMessageToService toService;
    @Resource
    private IZUserRoleService userRoleService;
    @Resource
    private IZUserService userService;

    /**
     * 发送消息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void send(MessageToBaseDTO msg) {
        // 消息主体
        ZMessage zMessage = new ZMessage();
        zMessage.setMsgId(GuidUtils.getUuid());
        zMessage.setMsgTitle(msg.getMsgTitle());
        zMessage.setMsgContent(msg.getMsgContent());
        zMessage.setMsgRouter(msg.getMsgRouter());
        zMessage.setPermissionName(msg.getPermissionName());
        zMessage.setCreateTime(LocalDateTime.now());
        save(zMessage);
        // 收信用户列表
        List<ZMessageTo> toList = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        if (msg.getToType().equals("user")) {
            userIds = msg.getToIds();
        } else if (msg.getToType().equals("role")) {
            // 角色查询用户
            userIds = userRoleService.lambdaQuery().in(ZUserRole::getRoleId, msg.getToIds()).list()
                    .stream().map(ru -> ru.getUserId()).collect(Collectors.toList());
        } else if (msg.getToType().equals("org")) {
            // 部门查询用户
            userIds = userService.lambdaQuery().in(ZUser::getOrgId, msg.getToIds()).list()
                    .stream().map(u -> u.getUserId()).collect(Collectors.toList());
        }
        userIds.stream().forEach(toUserId -> {
            ZMessageTo zMessageTo = new ZMessageTo();
            zMessageTo.setToId(GuidUtils.getUuid());
            zMessageTo.setMsgId(zMessage.getMsgId());
            zMessageTo.setSendUserId(CurrentUserUtils.getCurrentUser().getUserId());
            zMessageTo.setToUserId(toUserId);// 接收用户
            zMessageTo.setMsgStatus("0");
            zMessageTo.setCreateTime(LocalDateTime.now());
            toList.add(zMessageTo);
        });
        toService.saveBatch(toList);
    }

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZMessageDTO> pagelist(Integer page, Integer limit, String params) {
        // 查询条件
        HashMap<String, Object> paramsJson = JSONUtil.toBean(JSONUtil.parseObj(params), HashMap.class);
        paramsJson.put("userId", CurrentUserUtils.getCurrentUser().getUserId());
        Integer offset = (page - 1) * limit;
        // 查询列表
        List<ZMessageDTO> list = mapper.messageList(offset, limit, paramsJson);
        // 查询总数
        Integer count = mapper.messageListCount(paramsJson);
        // 组装查询结果
        Page<ZMessageDTO> result = new Page<>(page, limit);
        result.setRecords(list);
        result.setTotal(count);
        return result;
    }

    /**
     * 新增
     *
     * @param zMessageDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZMessageDTO zMessageDTO) {
        ZMessage zMessage = zMessageConvert.dtoToEntity(zMessageDTO);
        zMessage.setMsgId(GuidUtils.getUuid());
        zMessage.setCreateTime(LocalDateTime.now());
        save(zMessage);
    }

    /**
     * 修改
     *
     * @param zMessageDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZMessageDTO zMessageDTO) {
        ZMessage zMessage = zMessageConvert.dtoToEntity(zMessageDTO);
        updateById(zMessage);
    }

    /**
     * 删除关联用户，不删除消息主体
     *
     * @param idlist 删除id列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(List<String> idlist) {
        toService.lambdaUpdate().in(ZMessageTo::getMsgId, idlist)
                .eq(ZMessageTo::getToUserId, CurrentUserUtils.getCurrentUser().getUserId())
                .remove();
    }

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    @Override
    public String exportExcel(String params) {
        try {
            // 拼接导出Excel的文件，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<ZMessage> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("msgId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgId")), ZMessage::getMsgId, paramObj.getStr("msgId"));
                }
                if (paramObj.containsKey("msgTitle")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgTitle")), ZMessage::getMsgTitle, paramObj.getStr("msgTitle"));
                }
                if (paramObj.containsKey("msgContent")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgContent")), ZMessage::getMsgContent, paramObj.getStr("msgContent"));
                }
                if (paramObj.containsKey("msgRouter")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("msgRouter")), ZMessage::getMsgRouter, paramObj.getStr("msgRouter"));
                }
                if (paramObj.containsKey("permissionName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("permissionName")), ZMessage::getPermissionName, paramObj.getStr("permissionName"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZMessage::getCreateTime, paramObj.getStr("createTime"));
                }
            }
            List<ZMessage> list = list(wrapper);
            // 转换成导出excel实体
            List<ZMessageExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZMessageExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZMessageExcelOutDTO());
            }
            // 第一行标题
            String title = "消息中心";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZMessageExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void importExcel(HttpServletRequest request) {
        // 读取导入数据
        List<ZMessage> importData =
                ExcelReadUtils.read(request, 1, 2, ZMessage.class, ZMessageExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZMessage> saveData = importData.stream().map(o -> {
            o.setMsgId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

    @Override
    public MessageCountsDTO counts() {
        // 查询
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", CurrentUserUtils.getCurrentUser().getUserId());
        List<ZMessageDTO> list = mapper.messageList(null, null, params);
        // 结果
        MessageCountsDTO result = new MessageCountsDTO();
        result.setCount(list.size());
        result.setUnreadCount(list.stream().filter(d -> d.getMsgStatus().equals("0")).count());
        return result;
    }
}
