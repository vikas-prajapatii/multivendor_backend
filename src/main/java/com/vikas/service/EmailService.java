package com.vikas.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationOtpMail(
            String userEmail,
            String otp,
            String subject,
            String text) {

        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException | MailException e) {
            e.printStackTrace();
            throw new MailSendException("Failed to send email");
        }
    }

}