package com.wgx.mall.order;

import com.wgx.mall.order.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class MallOrderApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    void contextLoads() {
//        rabbitAdmin.declareExchange(new DirectExchange("test-exchange", true, false));
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setStatus(1);
        orderEntity.setTotalAmount(new BigDecimal(10000));
        rabbitTemplate.convertAndSend("amq.direct", "test-queue", orderEntity);
    }

}
