package com.felipe.belo.mvp.application;

import com.felipe.belo.mvp.MvpApplication;
import org.springframework.boot.SpringApplication;

public class TestMvpApplication {

    public static void main(String[] args) {
        SpringApplication.from(MvpApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
