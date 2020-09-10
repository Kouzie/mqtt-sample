package com.example.mtqq.component;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Profile;

@Slf4j
public class MqttMessageCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
        log.info("connection lost.....");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("message arrived, id:" + message.isDuplicate() + ", payload: " + new String(message.getPayload(), "UTF-8"));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("delivery complete....");
    }
}
