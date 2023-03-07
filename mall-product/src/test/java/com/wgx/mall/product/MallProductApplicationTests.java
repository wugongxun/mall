package com.wgx.mall.product;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.wgx.mall.product.entity.BrandEntity;
import com.wgx.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
class MallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private OSSClient ossClient;

    @Test
    void contextLoads() {
        ossClient.putObject("mall-wgx", "天选姬3.0静态PC出厂默认.jpg", new File("C:\\Users\\wgx\\OneDrive\\图片\\Saved Pictures\\天选姬3.0静态PC“出厂默认”.jpg"));
        System.out.println("上传成功");
    }

}
