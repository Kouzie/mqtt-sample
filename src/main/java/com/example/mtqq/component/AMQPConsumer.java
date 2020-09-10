package com.example.mtqq.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AMQPConsumer extends DefaultConsumer {
    private String consumerName;
    private ObjectMapper objectMapper;

    public AMQPConsumer(String consumerName, Channel channel) {
        super(channel);
        this.consumerName = consumerName;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws JsonProcessingException {
        String routingKey = envelope.getRoutingKey();
        String contentType = properties.getContentType();
        long deliveryTag = envelope.getDeliveryTag();
        // (process the message components here ...)
        log.info(consumerName + "- message body:" + new String(body) + ", routingKey:" + routingKey);
        log.info("headers:" + convertWithIteration(properties.getHeaders()));
//        log.info(consumerName + "- properties:" + objectMapper.writeValueAsString(properties));
        // 메세지 수신여부를 broker 에게 알림
        //this.getChannel().basicAck(deliveryTag, false);
    }

    public String convertWithIteration(Map<String, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        String connectionId = map.get("name").toString();
        for (String key : map.keySet()) {
            if (key.equals("variable_map"))
                log.info(map.get(key).toString());
            mapAsString.append(key + "=" + map.get(key) + ", ");
        }
        mapAsString.delete(mapAsString.length() - 2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }
}
