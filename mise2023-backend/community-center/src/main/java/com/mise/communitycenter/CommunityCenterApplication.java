package com.mise.communitycenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mise.communitycenter.mapper")
public class CommunityCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityCenterApplication.class, args);
    }

}
