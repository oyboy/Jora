package com.main.Jora.loger;

import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class KafkaAppender extends AppenderBase<ILoggingEvent> {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String topic = "logs";

    @Override
    protected void append(ILoggingEvent eventObject) {
        String jsonMessage = createJsonMessage(eventObject);
        if (jsonMessage != null) {
            System.out.println("Senind message: " + jsonMessage);
            kafkaTemplate.send(topic, jsonMessage);
        }
    }

    private String createJsonMessage(ILoggingEvent eventObject) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
        logMap.put("level", eventObject.getLevel().toString());
        logMap.put("thread", eventObject.getThreadName());
        logMap.put("logger", eventObject.getLoggerName());
        logMap.put("message", eventObject.getFormattedMessage());
        logMap.put("context", eventObject.getMDCPropertyMap());

        return logMapToJson(logMap);
    }

    private String logMapToJson(Map<String, Object> logMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(logMap);
        } catch (JsonProcessingException e) {
            addError("Failed to convert log map to JSON", e);
            return null;
        }
    }
}
