package com.sanger.sesaga.service;

import java.util.Calendar;

import javax.transaction.Transactional;

import com.sanger.sesaga.dao.PasswordResetTokenRepository;
import com.sanger.sesaga.entity.PasswordResetToken;
import com.sanger.sesaga.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);

    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        User user = passwordResetToken.getUser();
        if (user != null) {
            return user;
        }
        return null;
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken != null) {
            return isTokenExpired(passToken) ? false : true;
        }
        return false;

    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    @Override
    public void delete(String token) {
        PasswordResetToken passwordResetToken = getPasswordResetToken(token);
        if (passwordResetToken != null) {
            passwordResetTokenRepository.delete(passwordResetToken);
        }

    }

}
