package com.dcxiaolou.tinyJD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.dcxiaolou.tinyJD.order.mapper")
@SpringBootApplication
public class TinyjdOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyjdOrderServiceApplication.class, args);
    }

}
