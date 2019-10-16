package com.dcxiaolou.tinyJD.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(value = "com.dcxiaolou.tinyJD.manage.mapper")
public class TinyjdManageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyjdManageServiceApplication.class, args);
    }

}
