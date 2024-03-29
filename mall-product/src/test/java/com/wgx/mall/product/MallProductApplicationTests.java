package com.wgx.mall.product;

import com.wgx.mall.product.dao.AttrGroupDao;
import com.wgx.mall.product.service.BrandService;
import com.wgx.mall.product.service.CategoryService;
import com.wgx.mall.product.service.SkuSaleAttrValueService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    void contextLoads() {
//        RLock lock = redissonClient.getLock("lock");
//        lock.lock(10, TimeUnit.SECONDS);
//        try {
//            System.out.println(Thread.currentThread().getName() + "加锁成功");
//            Thread.sleep(30000);
//        } catch (Exception e) {
//
//        } finally {
//            System.out.println(Thread.currentThread().getName() + "释放锁");
//            lock.unlock();
//        }

//        System.out.println(attrGroupDao.getAttrGroupWithSpuId(10l, 225l));
        System.out.println(skuSaleAttrValueService.getSaleAttrsBySpuId(10l));
    }

}
