package com.wgx.mall.product;

import com.wgx.mall.product.service.BrandService;
import com.wgx.mall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest
class MallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
        System.out.println(redissonClient);
    }

}
