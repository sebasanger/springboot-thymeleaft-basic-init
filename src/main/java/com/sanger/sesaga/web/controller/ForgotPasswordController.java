package com.sanger.sesaga.web.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import com.sanger.sesaga.config.Utility;

import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.service.PasswordResetTokenService;
import com.sanger.sesaga.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    // Show the page to send email for restore password
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {

        return "forgot_password_form";
    }

    // form to send email and create token to restore password
    @PostMapping("/forgot_password")
    public String processForgotPasswordForm(HttpServletRequest request, Model model,
            RedirectAttributes redirectAttributes) {
        String email = request.getParameter("email");

        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("messageError", "Email not exist");
            return "forgot_password_form";
        }

        String token = RandomString.make(45);

        passwordResetTokenService.createPasswordResetTokenForUser(user, token);

        try {
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;

            sendEmail(email, resetPasswordLink);

            redirectAttributes.addFlashAttribute("messageSuccess", "Email Sended");

        } catch (Exception e) {
            model.addAttribute("messageError", "Internal error");
        }

        return "redirect:/showMyLoginPage";
    }

    private void sendEmail(String email, String resetPasswordLink)
            throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String subject = "here's the link to reset your password";
        String content = "<p>Hello,</p>";
        content += "<p>You have requested to reset you password</p>";
        content += "<p>Click the link to change your password: </p>";
        content += "<a href=\"" + resetPasswordLink + "\">Change my password</a>";
        content += "<p>Ignore this email if you remember your password or you dont made the request</p>";

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setFrom("sangercom.seba@gmail.com");

        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    private String showResetPasswordForm(@Param(value = "token") String token, Model model,
            RedirectAttributes redirectAttributes) {
        final boolean tokenIsValid = passwordResetTokenService.validatePasswordResetToken(token);

        if (!tokenIsValid) {
            redirectAttributes.addFlashAttribute("messageError", "Token invalid");

            return "redirect:/showMyLoginPage";
        }
        model.addAttribute("token", token);
        return "reset-password-form";
    }

    @PostMapping("/reset-password")
    public String updatePassword(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String passwordRepeat = request.getParameter("repeat-password");

        if (!password.equals(passwordRepeat)) {
            model.addAttribute("messageError", "Password not mismatch");
            model.addAttribute("token", token);
            return "reset-password-form";
        }

        final boolean tokenIsValid = passwordResetTokenService.validatePasswordResetToken(token);
        User user = passwordResetTokenService.getUserByPasswordResetToken(token);
        if (!tokenIsValid || user == null) {
            redirectAttributes.addFlashAttribute("messageError", "Token invalid");

            return "redirect:/showMyLoginPage";
        }

        userService.changeUserPassword(user, password);

        passwordResetTokenService.delete(token);

        redirectAttributes.addFlashAttribute("messageSuccess", "Password changed successfully");

        return "redirect:/showMyLoginPage";
    }
}
