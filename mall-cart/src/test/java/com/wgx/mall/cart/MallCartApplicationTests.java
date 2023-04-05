package com.wgx.mall.cart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

//@SpringBootTest
class MallCartApplicationTests {

    @Test
    void contextLoads() {
        BigDecimal zero = BigDecimal.ZERO;
        zero = zero.add(new BigDecimal(1000));
        System.out.println(zero);
    }

}
