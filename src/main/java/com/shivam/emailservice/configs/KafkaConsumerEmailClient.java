package com.shivam.emailservice.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.emailservice.dtos.SendEmailDto;
import com.shivam.emailservice.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Component
public class KafkaConsumerEmailClient {
    private ObjectMapper objectMapper;
    @Value("${sender.email}")
    private String senderEmail;
    @Value("${sender.password}")
    private String emailPassword;

    public KafkaConsumerEmailClient(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "signup", groupId = "EmailService")
    // from all the servers which are running under group EmailService
    // only one server should be able to handle this event
    public void handleSendEmailEvent(String message) {

        try {
            SendEmailDto sendEmailDto = objectMapper.readValue(message, SendEmailDto.class);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            //create Authenticator object to pass in Session.getInstance argument
            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, emailPassword);
                }
            };
            Session session = Session.getInstance(props, auth);

            EmailUtil.sendEmail(
                    session,
                    sendEmailDto.getTo(),
                    sendEmailDto.getSubject(),
                    sendEmailDto.getBody()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
