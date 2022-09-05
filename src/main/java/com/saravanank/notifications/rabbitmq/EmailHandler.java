package com.saravanank.notifications.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.saravanank.notifications.dto.MailRequest;
import com.saravanank.notifications.service.EmailService;
import com.saravanank.notifications.util.Json;

@Component
public class EmailHandler {
	
	@Autowired
	private EmailService emailService;

	@RabbitListener(queues = "notification-email")
	public void listenEmailQueue(String emailRequest) throws JsonMappingException, JsonProcessingException, IllegalArgumentException {
		MailRequest request = Json.fromJson(Json.parse(emailRequest), MailRequest.class); 
		
		Map<String, Object> maps = new HashMap<>();
		maps.put("appName", request.getApplicationName());
		maps.put("url", request.getUrl());
		maps.put("name", request.getName());
		System.out.println("Sending mail...");
		emailService.send(request, maps);
		
	}
	
}
