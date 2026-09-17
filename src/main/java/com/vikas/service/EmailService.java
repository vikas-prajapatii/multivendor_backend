package com.vikas.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) throws MessagingException, MailSendException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setSubject(subject);
            helper.setText(text + otp, true);
            helper.setTo(userEmail);
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendException("Failed to send email");
        }
    }

    public void sendVerificationOtpMail(String userEmail, String otp, String subject, String text) throws MessagingException, MailSendException {
        sendVerificationOtpEmail(userEmail, otp, subject, text);
    }

    public void sendOtp(String to, String otp) {
        try {
            sendVerificationOtpEmail(to, otp, "Noir Bazaar - Verify Your Email", "Your verification OTP is: " + otp);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    public void sendPasswordResetLink(String to, String resetLink) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setSubject("Noir Bazaar - Reset Your Password");
            helper.setText("Please click the link below to reset your password:\n" + resetLink, false);
            helper.setTo(to);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Failed to send reset email: " + e.getMessage());
        }
    }
}
