//package com.example.medai.services;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    @Value("${COMPANY_EMAIL:}")
//    private String COMPANY_EMAIL;
//    private final JavaMailSender mailSender;
//
//    public void sendRegistrationEmail(String email, String otp) {
//
//        String body = """
//                <h1>Welcome to Signaro</h1>
//                <p>Your verification code is: <b>%s</b></p>
//                """.formatted(otp);
//
//        String subject = "Welcome to Signaro!";
//        sendEmail(COMPANY_EMAIL, email, subject ,body);
//    }
//
//    @Async
//    public void sendEmail(String from, String to, String subject, String body) {
//
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message,true, "UTF-8" );
//            helper.setFrom(from);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true); // true = HTML
//
//            mailSender.send(message);
//        } catch(MessagingException e) {
//            log.error("Error sending email", e);
//        }
//
//
//    }
//}
