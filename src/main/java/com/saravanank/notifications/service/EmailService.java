package com.saravanank.notifications.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.saravanank.notifications.dto.MailRequest;
import com.saravanank.notifications.dto.MailResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Configuration config;

	@Value("${spring.mail.username}")
	private String from;

	public MailResponse send(MailRequest request, Map<String, Object> model) {
		System.out.println("In service");
		MailResponse response = new MailResponse();
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			Template t = config.getTemplate("account-activation.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(request.getTo());
			helper.setText(html, true);
			helper.setSubject(request.getSubject());
			if (request.getFrom() != null && !request.getFrom().equals(""))
				helper.setFrom(request.getFrom());
			else
				helper.setFrom(from);
			mailSender.send(message);

			System.out.println("Mail sent");
			response.setMessage("Mail sent to: " + request.getTo());
			response.setStatus(true);
		} catch (MessagingException | IOException | TemplateException exp) {
			System.out.println("Error sending " + exp.getMessage());
			exp.getStackTrace();
			response.setMessage("Mail sending failure : " + exp.getMessage());
			response.setStatus(false);
		}
		System.out.println("Returned");
		return response;
	}
}
