package com.sanger.sesaga.web.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sanger.sesaga.entity.Role;
import com.sanger.sesaga.validation.ValidEmail;

public class UserDto {
    private Long id;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    private String firstName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.lastName}")
    private String lastName;

    private String password;

    // @NotNull
    // @Size(min = 1)
    private String matchingPassword;

    private boolean enable;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.userName}")
    private String userName;

    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(final String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "UserDto [email=" + email + ", firstName=" + firstName + ", id=" + id + ", lastName=" + lastName
                + ", matchingPassword=" + matchingPassword + ", password=" + password + ", roles=" + roles
                + ", userName=" + userName + "]";
    }

}
