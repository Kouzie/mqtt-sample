package com.example.mtqq.bean;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;
@Profile("spring-amqp")
public class SpringAMQPSender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private TopicExchange topic;

    AtomicInteger index = new AtomicInteger(0);

    private final String[] topics = {
            ".computer.part.cpu",
            ".computer.part.monitor",
            ".computer.part.keyboard",
            ".computer.part.gpu",
            ".computer.part.ram"};

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        if (this.index.incrementAndGet() == topics.length) this.index.set(0);
        String key = topics[this.index.get()];
        String message = "Hello RabbitMQ, key:" + key + ", index:" + index;
        template.convertAndSend(topic.getName(), key, message.getBytes());
        System.out.println("send message:" + message);
    }
}
