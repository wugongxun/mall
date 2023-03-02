package com.wgx.mall.coupon;

import com.wgx.mall.coupon.entity.CouponEntity;
import com.wgx.mall.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MallCouponApplicationTests {

    @Resource
    private CouponService couponService;

    @Test
    void contextLoads() {
        CouponEntity coupon = new CouponEntity();
        coupon.setCouponName("test");
        couponService.save(coupon);
    }

}
