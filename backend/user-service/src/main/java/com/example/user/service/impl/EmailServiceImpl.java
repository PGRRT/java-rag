package com.example.user.service.impl;

import com.example.user.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${app.company.email:}")
    private String COMPANY_EMAIL;
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendRegistrationEmail(String email, String otp) {
        String body = """
                <h1>Welcome to Med AI</h1>
                <p>Your verification code is: <b>%s</b></p>
                """.formatted(otp);

        String subject = "Welcome to Med AI!";

        sendEmail(COMPANY_EMAIL, email, subject ,body);
    }

    @Override
    public void sendEmail(String from, String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true, "UTF-8" );
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            mailSender.send(message);
        } catch(MessagingException e) {
            log.error("Error sending email", e);
        }
    }
}
