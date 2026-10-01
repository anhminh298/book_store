package com.nhm.bookstore.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService_24162073 {
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    public static boolean sendOTP(String toEmail, String otpCode) {
        String email = setting("bookstore.smtp.user", "BOOKSTORE_SMTP_USER");
        String password = setting("bookstore.smtp.password", "BOOKSTORE_SMTP_PASSWORD");
        if (email == null || password == null) return false;

        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã OTP xác thực tài khoản BookStore");
            message.setText("Mã OTP của bạn là: " + otpCode + "\nVui lòng không chia sẻ mã này cho bất kỳ ai.");
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    private static String setting(String property, String environmentVariable) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) value = System.getenv(environmentVariable);
        return value == null || value.isBlank() ? null : value;
    }
}
