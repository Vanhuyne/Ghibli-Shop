package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.service.AccountService;
import com.mvc.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class LoginController {

    private final CartService cartService;
    private final AccountService accountService;
    private final HttpSession httpSession;


    @GetMapping("/login")
    public String showLoginForm() {
        return (httpSession.getAttribute("loggedInUser") != null) ? "redirect:/products" : "user/login";
    }

    @GetMapping("/view-profile/{username}")
    public String viewProfile(@PathVariable(required = false) String username, Model model) {
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        if (username != null && !username.isEmpty()) {
            Account user = accountService.getUserByUsername(username);
            if (user == null) {
                return "redirect:/login";
            }
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("user", user);
            model.addAttribute("sizeCart", cartService.getSizeCart(username));
        }
        return "user/edit_profile";
    }

    @PostMapping("/profile/edit-image")
    public String editProfilePhoto(@ModelAttribute("user") Account user,
                                   @RequestParam("profilePicture") MultipartFile profilePicture, Model model) {
        Account sessionAccount = (Account) httpSession.getAttribute("loggedInUser");
        if (user != null && sessionAccount != null) {
            String username = user.getUsername();

            accountService.updateProfilePicture(user, profilePicture);

            // Retrieve the updated user from the database
            Account updatedUser = accountService.getUserByUsername(username);

            // Set the updated user object in the model
            model.addAttribute("user", updatedUser);


            model.addAttribute("loggedInUser", sessionAccount);
            return "redirect:/view-profile/" + username;
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/profile/edit-info")
    public String editProfileInfo(@ModelAttribute("user") Account user, Model model, RedirectAttributes redirectAttributes) {
        Account sessionAccount = (Account) httpSession.getAttribute("loggedInUser");
        if (user != null && sessionAccount != null) {
            String username = user.getUsername();

            accountService.updateProfileInfo(user);

            // Retrieve the updated user from the database
            Account updatedUser = accountService.getUserByUsername(user.getUsername());

            // Set the updated user object in the model
            model.addAttribute("user", updatedUser);

            model.addAttribute("loggedInUser", sessionAccount);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/view-profile/" + username;
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "user/error";
    }


}
