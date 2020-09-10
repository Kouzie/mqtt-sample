package com.example.mtqq.component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

// rabbitmq-plugins enable rabbitmq_event_exchange
// rabbitmq-plugins enable rabbitmq_mqtt
@Slf4j
@Profile("amqp")
@Component
public class AMQPComponent {

    @Autowired
    private ConnectionFactory factory;
    @Value("${client.id}")
    private String clientId;

    private String amqExchangeName = "amq.topic";
    private String eventExchangeName = "amq.rabbitmq.event";

    private Channel amqChannel;
    private Channel eventChannel;

    @PostConstruct
    public void init() {
        log.info("AMQPComponent init begin. " + clientId);
        try {
            String amqQueueName = "mqtt_to_amqp";
            String evenQueueName= "event_queue";
            log.info("init queueName:" + amqQueueName);
            Connection amqConn = factory.newConnection("amq-connection");
            Connection eventConn = factory.newConnection("event-connection");
            amqChannel = amqConn.createChannel();
            eventChannel = eventConn.createChannel();
            //queueName, durable,  exclusive, autoDelete, arguments
            amqQueueName = amqChannel.queueDeclare(amqQueueName, true, false, false, null).getQueue();
            evenQueueName = eventChannel.queueDeclare(evenQueueName, true, false, false, null).getQueue();
            // queueName: 큐의 이름
            // durable: 서버 재시작에도 살아남을 튼튼한(?) 큐로 선언할 것인지 여부
            // exclusive: 현재의 연결에 한정되는 배타적인 큐로 선언할 것인지 여부
            // autoDelete: 사용되지 않을 때 서버에 의해 자동 삭제되는 큐로 선언할 것인지 여부
            // arguments: 큐를 구성하는 다른 속성
            log.info("bind queueName:" + amqQueueName);
            //channel.queueBind(queueName, exchangeName, ".computer.part.cpu");
            //channel.queueBind(queueName, exchangeName, ".computer.part.monitor");
            //channel.queueBind(queueName, exchangeName, ".computer.part.keyboard");
            //channel.queueBind(queueName, exchangeName, ".computer.part.gpu");
            //channel.queueBind(queueName, exchangeName, ".computer.part.ram");
            //channel.queueBind(queueName, exchangeName, ".computer.part.*");
            amqChannel.queueBind(amqQueueName, amqExchangeName, "#");
            eventChannel.queueBind(evenQueueName, eventExchangeName ,"connection.closed");
            eventChannel.queueBind(evenQueueName, eventExchangeName ,"connection.created");
            // kafka 에선 일정 시간마다 받은 메세지를 모두 수신처리 하기 위한 역할을 수행, rabbitMQ도 비슷한듯?
            boolean autoAck = false;
            amqChannel.basicConsume(amqQueueName, autoAck, new AMQPConsumer(amqQueueName, amqChannel));
            eventChannel.basicConsume(evenQueueName, autoAck, new AMQPConsumer(evenQueueName, eventChannel));
            // channel.basicCancel(queueName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public boolean publishingMessage(String message, String routingKey) {
        try {
            byte[] messageBodyBytes = message.getBytes();
            boolean mandatory;
            // null부분은 basicProperties 가 들어가는데 header 와 같은 역할이다.
            amqChannel.basicPublish(amqExchangeName, routingKey, null, messageBodyBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
