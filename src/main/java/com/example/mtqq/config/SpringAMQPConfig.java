package com.example.mtqq.config;

import com.example.mtqq.bean.SpringAMQPReceiver;
import com.example.mtqq.bean.SpringAMQPSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("spring-amqp")
@Configuration
public class SpringAMQPConfig {

    String queueName = "mqtt_to_amqp";
    
    @Bean
    public TopicExchange exchangeTopic() {
        return new TopicExchange("amq.topic");
    }

    @Bean
    public Queue queue() {
        // durable, exclusive, autoDelete
        return new Queue(queueName, true, false, false);
    }

    @Bean
    public SpringAMQPReceiver springAMQPReceiver() {
        return new SpringAMQPReceiver();
    }

    @Bean
    public SpringAMQPSender springAMQPSender() {
        return new SpringAMQPSender();
    }

    @Bean
    public Binding binding(TopicExchange exchangeTopic, Queue queue) throws JsonProcessingException {
        log.info("biding invoked, queueName:" + queue.getName());
        log.info("queue:" + new ObjectMapper().writeValueAsString(queue.getArguments()));
        return BindingBuilder.bind(queue).to(exchangeTopic).with(".computer.part.*");
    }
}