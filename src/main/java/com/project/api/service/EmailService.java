package com.project.api.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Scope(value = "prototype")
public class EmailService {

    @Value("${email.from}")
    private String from;

    private String to;

    @Value("${email.password}")
    private String password;

    private String host = "smtp.gmail.com";
    private String port = "465";
    private Properties properties = System.getProperties();
    private Session session;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    /**
     * Initializes the email service with the provided recipient email address.
     *
     * @param to The recipient email address.
     */
    private void init(String to) {
        this.to = to;

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        session.setDebug(true);
    }

    /**
     * Sends an email with the provided OTP to the recipient.
     *
     * @param to  The recipient email address.
     * @param OTP The OTP to be sent.
     * @return True if the email was sent successfully, false otherwise.
     */
    public Boolean sendOTPEmail(String to, String OTP) {
        try {
            init(to);

            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field.
            message.setSubject("Password Verification Code " + OTP);

            // Set the actual message.
            message.setText("This is your OTP: " + OTP);

            // Send the message.
            Transport.send(message);

            LOGGER.info("OTP email sent successfully to: {}", to);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Error occurred while sending OTP email to {}: {}", to, ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
