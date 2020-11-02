package com.sanger.sesaga.service;

import com.sanger.sesaga.entity.PasswordResetToken;
import com.sanger.sesaga.entity.User;

public interface PasswordResetTokenService {

    void createPasswordResetTokenForUser(User user, String token);

    User getUserByPasswordResetToken(String token);

    PasswordResetToken getPasswordResetToken(String token);

    boolean validatePasswordResetToken(String token);

    void delete(String token);

}
