package com.example.mtqq.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("spring-amqp")
public class SpringAMQPReceiver {

    // bean factory 에서 queue 이름의 빈객체를 찾아 등록
    @RabbitListener(queues = "#{queue.name}")
    public void receive(byte[] payload) throws InterruptedException {
        log.info("message:" + new String(payload));
    }
}