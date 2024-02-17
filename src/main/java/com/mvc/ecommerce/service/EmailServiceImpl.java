package com.mvc.ecommerce.service;

import com.mvc.ecommerce.utils.SendEmailUtils;
import com.mvc.ecommerce.entity.Account;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private static final String EMAIL_WELCOME_SUBJECT = "Welcome to Ecommerce";
    private static final String EMAIL_FORGOT_PASSWORD = "Ecommerce - Your Password";
    private static final String EMAIL_SHARE_PRODUCTS = "Ecommerce - Another has just send the video to you!!!";

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String user;

    @Value("${spring.mail.password}")
    private String pass;

    @Override
    public void sendMail(ServletContext context, Account recipient, String type) {
        try {
            String content;
            String subject;
            switch (type) {
                case "welcome":
                    subject = EMAIL_WELCOME_SUBJECT;
                    content = "Dear " + recipient.getUsername() + " hope you have best time!!";
                    break;
                case "forgot":
                    subject = EMAIL_FORGOT_PASSWORD;
                    content = "Dear " + recipient.getUsername() + ", your password is here: " + recipient.getPassword();
                    break;
                case "share":
                    subject = EMAIL_SHARE_PRODUCTS;
                    content = recipient.getUsername() + "has just send the video for you!";
                    break;
                default:
                    subject = "Ecommerce ";
                    content = "This email is not exist ! Check it!!";
            }
            SendEmailUtils.sendEmail(host, port, user, pass, recipient.getEmail(), subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
