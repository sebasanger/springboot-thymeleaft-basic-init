package com.sanger.sesaga.web.controller;

import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.service.VerificationTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ValidationTokenController {

    @Autowired
    private VerificationTokenService verificationTokenService;

    @GetMapping("/validate-acount")
    private String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        final String tokenResult = verificationTokenService.validateVerificationToken(token);

        if (tokenResult.equals("invalidToken")) {
            model.addAttribute("messageError", "Token invalid");
        } else if (tokenResult.equals("expired")) {
            model.addAttribute("messageError", "Token expired");
        } else if (tokenResult.equals("valid")) {
            model.addAttribute("messageSuccess", "Your acount was valdiated");
            User user = verificationTokenService.getUser(token);
            verificationTokenService.validateUser(user.getId());
        }

        return "validation-acount";
    }

    @GetMapping("/validar")
    private String showResetPasswordForm(Model model) {

        return "validation-acount";
    }

}
