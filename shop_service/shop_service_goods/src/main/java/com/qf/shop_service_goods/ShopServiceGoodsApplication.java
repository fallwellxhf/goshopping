package com.qf.shop_service_goods;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.rabbitmq.client.AMQP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.qf")
@MapperScan("com.qf.dao")
@DubboComponentScan("com.qf.serviceimpl")
public class ShopServiceGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopServiceGoodsApplication.class, args);
    }

}
