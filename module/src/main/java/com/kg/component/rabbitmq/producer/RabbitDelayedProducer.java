package com.kg.component.rabbitmq.producer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ delay 延迟模式 - 工具类
 *
 * @author ziro
 * @date 2024/8/6 16:53
 */
@Component
@RabbitIsEnabled
public class RabbitDelayedProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitBinding binding;

    /**
     * 默认 delay exchange 名称
     */
    private static final String DEFAULT_DELAY_EXCHANGE = "dashu.delay.exchange";
    /**
     * 默认 delay routeKey 路由
     */
    private static final String DEFAULT_DELAY_ROUTE_KEY = "dashu.delay.queue";

    /**
     * 发消息 - 默认交换机，默认路由
     *
     * @param message   消息内容
     * @param delayTime 延迟时间，单位：秒
     */
    public void send(String message, long delayTime) {
        send(message, DEFAULT_DELAY_ROUTE_KEY, DEFAULT_DELAY_EXCHANGE, DEFAULT_DELAY_ROUTE_KEY, delayTime);
    }

    /**
     * 发消息 - 指定路由
     *
     * @param message   消息内容
     * @param routeKey  路由（路由 = 队列名）
     * @param delayTime 延迟时间，单位：秒
     */
    public void send(String message, String routeKey, long delayTime) {
        send(message, routeKey, DEFAULT_DELAY_EXCHANGE, routeKey, delayTime);
    }

    /**
     * 发消息 - 指定路由、指定交换机
     *
     * @param message   消息内容
     * @param routeKey  路由（路由 = 队列名）
     * @param exchange  交换机名称
     * @param delayTime 延迟时间，单位：秒
     */
    public void send(String message, String routeKey, String exchange, long delayTime) {
        send(message, routeKey, exchange, routeKey, delayTime);
    }

    /**
     * 发消息 - 指定路由、指定交换机
     *
     * @param message   消息内容
     * @param routeKey  路由（路由 ≠ 队列名）
     * @param exchange  交换机名称
     * @param queue     队列名称
     * @param delayTime 延迟时间，单位：秒
     */
    public void send(String message, String routeKey, String exchange, String queue, long delayTime) {
        // 绑定队列到交换机
        // 注意：若无队列或交换机或绑定关系，直接[发消息]会报错，防止意外，先绑定一次
        binding.bindDelay(exchange, routeKey, queue);
        // 设置消息延迟时间
        MessagePostProcessor messagePostProcessor = msg -> {
            msg.getMessageProperties().setHeader("x-delay", delayTime * 1000); // 延迟时间单位为毫秒
            return msg;
        };
        // 发送消息（delay 模式，必须指定routingKey）
        rabbitTemplate.convertAndSend(exchange, routeKey, message, messagePostProcessor);
    }

}
