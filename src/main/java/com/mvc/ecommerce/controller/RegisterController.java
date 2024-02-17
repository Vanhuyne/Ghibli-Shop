package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class RegisterController {

    private final AccountService accountService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Account());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Validated Account account,
                           BindingResult bindingResult, Model model,
                           @RequestParam("confirmPassword") String confirmPassword) {
        if (bindingResult.hasErrors()) {
            // If there are validation errors, return to the registration form with error messages
            return "user/register";
        }
        if (!account.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Password does not match Confirm Password");
            return "user/register";
        }
        try {
            // Call the service to register the user
            accountService.registerUser(account);
        } catch (IllegalArgumentException e) {
            // Handle specific validation exceptions
            bindingResult.rejectValue("username", "error.account", e.getMessage());
            return "user/register";
        }
        model.addAttribute("success", "Logged in successfully.Logging in now");
        return "user/register";
    }
}
