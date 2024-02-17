package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Account;
import jakarta.servlet.ServletContext;


public interface EmailService {
    void sendMail(ServletContext context, Account recipient, String type);
}
