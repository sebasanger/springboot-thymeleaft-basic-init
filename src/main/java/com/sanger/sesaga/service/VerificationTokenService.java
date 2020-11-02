package com.sanger.sesaga.service;

import com.sanger.sesaga.entity.User;
import com.sanger.sesaga.entity.VerificationToken;

public interface VerificationTokenService {
    void createVerificationTokenForUser(String username);

    VerificationToken getVerificationTokenByUser(User user);

    String validateVerificationToken(String token);

    User getUser(String verificationToken);

    void sendEmailVerification(String username, String url, String password);

    void validateUser(Long id);

}
