package com.life.progress;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.life.progress.mapper")
public class LifeProgressApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeProgressApplication.class, args);
        System.out.println("==============================================");
        System.out.println("   人生进度条系统启动成功！");
        System.out.println("   访问地址: http://localhost:8080/api");
        System.out.println("==============================================");
    }
}
