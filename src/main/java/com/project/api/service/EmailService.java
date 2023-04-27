package com.project.api.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Scope(value="prototype")
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

    private void init(String to){

        this.to = to;

        this.properties.put("mail.smtp.host", host);
        this.properties.put("mail.smtp.port", port);
        this.properties.put("mail.smtp.ssl.enable", "true");
        this.properties.put("mail.smtp.auth", "true");

        session = Session.getInstance(properties, new Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        session.setDebug(true);
    }

    public Boolean sendOTPEmail(String to,String OTP){
        try{
            init(to);
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Password Verification Code " + OTP);

            // Now set the actual message
            message.setText("This is your OTP: " + OTP);
            // Send message
            Transport.send(message);
            return true;
        }
        catch (Exception ex){
            System.out.println("ERROR " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
