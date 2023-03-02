package com.wgx.mall.product;

import com.wgx.mall.product.entity.BrandEntity;
import com.wgx.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brand = new BrandEntity();
        brand.setBrandId(1l);
        brand.setName("Test");

        brandService.updateById(brand);
    }

}
