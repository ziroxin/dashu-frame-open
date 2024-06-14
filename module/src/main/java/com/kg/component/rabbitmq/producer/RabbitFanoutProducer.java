package com.kg.component.rabbitmq.producer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ Fanout模式工具类
 *
 * @author ziro
 * @date 2024/6/13 15:27
 */
@Component
@RabbitIsEnabled
public class RabbitFanoutProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RabbitBinding binding;

    /**
     * 默认 fanout exchange 名称
     */
    private static final String DEFAULT_FANOUT_EXCHANGE = "dashu.fanout.exchange";
    /**
     * 默认 fanout queue 名称
     */
    private static final String DEFAULT_FANOUT_QUEUE = "dashu.fanout.queue";

    /**
     * 广播消息 - 默认交换机
     *
     * @param message 消息内容
     */
    public void send(String message) {
        send(message, DEFAULT_FANOUT_EXCHANGE, DEFAULT_FANOUT_QUEUE);
    }

    /**
     * 广播消息 - 指定交换机
     *
     * @param message  消息内容
     * @param exchange 指定交换机名称
     */
    public void send(String message, String exchange) {
        send(message, exchange, exchange + ".queue");
    }

    /**
     * 广播消息 - 指定交换机，并绑定指定队列
     *
     * @param message  消息内容
     * @param exchange 指定交换机名称
     * @param queue    指定队列名称
     */
    public void send(String message, String exchange, String queue) {
        // 绑定队列到交换机
        // 注意：若无队列或交换机或绑定关系，直接[发消息]会报错，防止意外，先绑定一次
        binding.bindFanout(exchange, queue);
        // 广播模式，不需要指定 routing key，会广播到全部队列
        rabbitTemplate.convertAndSend(exchange, "", message);
    }
}
