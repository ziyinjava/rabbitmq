package com.ziyin.rabbitmq.springboot.consumer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.ziyin.rabbitmq.springboot.consumer.*"})
public class ConsumerConfig {

}
