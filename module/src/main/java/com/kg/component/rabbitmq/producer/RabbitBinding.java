package com.kg.component.rabbitmq.producer;

import com.kg.component.rabbitmq.enable.RabbitIsEnabled;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Rabbit MQ 绑定 队列 和 交换机的工具类
 *
 * @author ziro
 * @date 2024/6/14 10:10
 */
@Component
@RabbitIsEnabled
public class RabbitBinding {
    @Resource
    private RabbitAdmin rabbitAdmin;

    /**
     * 绑定队列到fanout广播交换机
     *
     * @param exchange 交换机名称
     * @param queue    队列名称
     */
    public void bindFanout(String exchange, String queue) {
        // 创建 fanout exchange
        FanoutExchange fanoutExchange = new FanoutExchange(exchange);
        // 创建队列
        Queue queueObj = new Queue(queue);
        // 绑定队列到交换机
        rabbitAdmin.declareExchange(fanoutExchange);
        rabbitAdmin.declareQueue(queueObj);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queueObj).to(fanoutExchange));
    }

    /**
     * 绑定队列到direct交换机
     *
     * @param exchange   交换机名称
     * @param routingKey 路由（路由 = 队列名）
     */
    public void bindDirect(String exchange, String routingKey) {
        bindDirect(exchange, routingKey, routingKey);
    }

    /**
     * 绑定队列到direct交换机
     *
     * @param exchange   交换机名称
     * @param routingKey 路由（路由 ≠ 队列名）
     * @param queue      队列名称
     */
    public void bindDirect(String exchange, String routingKey, String queue) {
        // 创建 direct exchange
        DirectExchange directExchange = new DirectExchange(exchange);
        // 创建队列
        Queue queueObj = new Queue(queue);
        // 绑定队列到交换机
        rabbitAdmin.declareExchange(directExchange);
        rabbitAdmin.declareQueue(queueObj);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queueObj).to(directExchange).with(routingKey));
    }
}
