package com.wgx.mall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;

/**
 * @author wgx
 * @since 2023/4/4 17:16
 */
@Configuration
@EnableRabbit
@Slf4j
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //定制RabbitTemplate
    @PostConstruct
    public void initRabbitTemplate() {
        /**
         * @param correlationData 当前消息的唯一关联数据（消息的唯一id）
         * @param ack 消息是否收到
         * @param cause 失败的原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息丢失:{},原因:{}", new String(correlationData.getReturned().getMessage().getBody()), cause);
            }
        });

        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            log.error("消息丢失:{},原因:{}", new String(returnedMessage.getMessage().getBody()), returnedMessage.getReplyText());
        });

        //设置Message的序列化方式为json
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }




    //注入RabbitAdmin
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}
