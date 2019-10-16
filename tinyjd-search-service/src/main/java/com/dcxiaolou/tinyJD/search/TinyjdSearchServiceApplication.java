package com.dcxiaolou.tinyJD.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.dcxiaolou.tinyJD.search.mapper")
@SpringBootApplication
public class TinyjdSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyjdSearchServiceApplication.class, args);
    }

}
