package com.dcxiaolou.tinyJD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.dcxiaolou.tinyJD.payment.mapper")
@SpringBootApplication
public class TinyjdPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyjdPaymentApplication.class, args);
    }

}
