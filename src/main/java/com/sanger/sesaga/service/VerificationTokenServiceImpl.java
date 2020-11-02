package com.sanger.sesaga.service;

import java.util.Calendar;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import com.sanger.sesaga.dao.UserDao;
import com.sanger.sesaga.dao.UserRepository;
import com.sanger.sesaga.dao.VerificationTokenRepository;
import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.entity.VerificationToken;
import com.sanger.sesaga.web.error.EmailSendException;
import com.sanger.sesaga.web.error.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional

public class VerificationTokenServiceImpl implements VerificationTokenService {

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void createVerificationTokenForUser(String username) {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        String token = RandomString.make(45);
        final VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);

    }

    @Override
    public User getUser(String verificationToken) {
        final VerificationToken token = verificationTokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }

        return null;
    }

    @Override
    public VerificationToken getVerificationTokenByUser(User user) {
        return verificationTokenRepository.findByUser(user);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnable(true);
        // tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public void sendEmailVerification(String username, String url, String password) {
        User user = userRepository.findByUserName(username);
        String token = getVerificationTokenByUser(user).getToken();
        String link = url + "/validate-acount?token=" + token;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String subject = "here's the link to validate your acount";
        String content = "<p>Hello,</p>";
        content += "<p>Click the link to verificate your acount: </p>";
        content += "<a href=\"" + link + "\">Verificate</a>";
        content += "<p>Your password is: <b>" + password + "</b> please change it after entering the site </p>";
        content += "<p>Ignore this email if you remember your password or you dont made the request</p>";

        try {
            // helper.setTo(user.getEmail());
            helper.setTo("seba_sanger@hotmail.com");
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom("sangercom.seba@gmail.com");
        } catch (MessagingException e) {
            throw new EmailSendException("Error on send email");
        }

        mailSender.send(message);

    }

    @Override
    public void validateUser(Long id) {
        userDao.validate(id);
    }

}
