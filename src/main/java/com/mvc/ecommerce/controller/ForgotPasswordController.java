package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.service.AccountService;
import com.mvc.ecommerce.service.EmailService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {

    private ServletContext servletContext;
    private final AccountService accountService;
    private final EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "user/forgot";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<Account> optionalAccount = accountService.getAccountByEmail(email);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            emailService.sendMail(servletContext, account, "forgot");
            return "redirect:/forgot-password?success";
        } else {
            model.addAttribute("error", "Account not found for the given email");
            return "redirect:/forgot-password?error";
        }

    }

}
