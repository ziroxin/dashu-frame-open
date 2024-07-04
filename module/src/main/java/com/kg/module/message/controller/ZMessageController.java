package com.kg.module.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kg.core.annotation.AutoOperateLog;
import com.kg.core.annotation.NoRepeatSubmit;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.module.message.dto.MessageCountsDTO;
import com.kg.module.message.dto.ZMessageDTO;
import com.kg.module.message.dto.convert.ZMessageConvert;
import com.kg.module.message.service.ZMessageService;
import com.kg.module.messageTo.entity.ZMessageTo;
import com.kg.module.messageTo.service.ZMessageToService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * 消息中心 前端控制器
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@RestController
@RequestMapping("/message/zMessage")
@Api(tags = "ZMessageController", value = "消息中心", description = "消息中心")
public class ZMessageController {

    @Resource
    private ZMessageService messageService;
    @Resource
    private ZMessageConvert zMessageConvert;
    @Resource
    private ZMessageToService messageToService;

    @ApiOperation(value = "/message/zMessage/counts", notes = "消息总数查询-消息中心", httpMethod = "GET")
    @GetMapping("/counts")
    public MessageCountsDTO counts() {
        return messageService.counts();
    }

    @ApiOperation(value = "/message/zMessage/read", notes = "标记已读-消息中心", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "msgId", value = "ID", paramType = "query", required = true, dataType = "String")})
    @GetMapping("/read")
    public void read(String msgId) {
        messageToService.lambdaUpdate().set(ZMessageTo::getMsgStatus, "1") // 标记已读
                .set(ZMessageTo::getReadTime, LocalDateTime.now())
                .eq(ZMessageTo::getMsgId, msgId)
                .eq(ZMessageTo::getToUserId, CurrentUserUtils.getCurrentUser().getUserId())
                .update();
    }

    @ApiOperation(value = "/message/zMessage/readAll", notes = "全部标记已读-消息中心", httpMethod = "GET")
    @ApiImplicitParams({})
    @GetMapping("/readAll")
    public void readAll() {
        messageToService.lambdaUpdate().set(ZMessageTo::getMsgStatus, "1") // 标记已读
                .set(ZMessageTo::getReadTime, LocalDateTime.now())
                .eq(ZMessageTo::getToUserId, CurrentUserUtils.getCurrentUser().getUserId())
                .update();
    }

    @ApiOperation(value = "/message/zMessage/list", notes = "分页列表-消息中心", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value = "当前页码", paramType = "query", required = false, dataType = "Integer"), @ApiImplicitParam(name = "limit", value = "每页条数", paramType = "query", required = false, dataType = "Integer"), @ApiImplicitParam(name = "params", value = "查询条件", paramType = "query", required = false, dataType = "String")})
    @GetMapping("/list")
    public Page<ZMessageDTO> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit, @RequestParam(value = "params", required = false) String params) {
        return messageService.pagelist(page, limit, params);
    }

    @ApiOperation(value = "/message/zMessage/delete", notes = "删除-消息中心", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgIds", value = "待删除id列表", paramType = "body", required = true, allowMultiple = true, dataType = "String")
    })
    @DeleteMapping("/delete")
    @NoRepeatSubmit
    @AutoOperateLog(logMethod = "/message/zMessage/delete", logMsg = "删除-消息中心")
    public void delete(@RequestBody String[] msgIds) throws BaseException {
        try {
            messageService.delete(Arrays.asList(msgIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage() != null ? e.getMessage() : "删除失败！请重试");
        }
    }
}
