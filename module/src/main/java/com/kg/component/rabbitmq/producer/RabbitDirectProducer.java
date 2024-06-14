package com.kg.component.rabbitmq.producer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ Direct模式工具类
 *
 * @author ziro
 * @date 2024/6/13 15:27
 */
@Component
@RabbitIsEnabled
public class RabbitDirectProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitBinding binding;

    /**
     * 默认 direct exchange 名称
     */
    private static final String DEFAULT_DIRECT_EXCHANGE = "dashu.direct.exchange";
    /**
     * 默认 direct routeKey 路由
     */
    private static final String DEFAULT_DIRECT_ROUTE_KEY = "dashu.direct.route";

    /**
     * 发消息 - 默认交换机，默认路由
     *
     * @param message 消息内容
     */
    public void send(String message) {
        send(message, DEFAULT_DIRECT_ROUTE_KEY, DEFAULT_DIRECT_EXCHANGE);
    }

    /**
     * 发消息 - 指定路由
     *
     * @param message  消息内容
     * @param routeKey 路由（路由 = 队列名）
     */
    public void send(String message, String routeKey) {
        send(message, routeKey, DEFAULT_DIRECT_EXCHANGE);
    }

    /**
     * 发消息 - 指定路由、指定交换机
     *
     * @param message  消息内容
     * @param routeKey 路由（路由 = 队列名）
     * @param exchange 交换机名称
     */
    public void send(String message, String routeKey, String exchange) {
        send(message, routeKey, exchange, routeKey);
    }

    /**
     * 发消息 - 指定路由、指定交换机、指定队列
     * 路由 ≠ 队列名 时适用
     *
     * @param message  消息内容
     * @param routeKey 路由（路由 ≠ 队列名）
     * @param exchange 交换机名称
     * @param queue    队列名称
     */
    public void send(String message, String routeKey, String exchange, String queue) {
        // 绑定队列到交换机
        // 注意：若无队列或交换机或绑定关系，直接[发消息]会报错，防止意外，先绑定一次
        binding.bindDirect(exchange, routeKey, queue);
        // direct 模式，必须指定routingKey
        rabbitTemplate.convertAndSend(exchange, routeKey, message);
    }
}
