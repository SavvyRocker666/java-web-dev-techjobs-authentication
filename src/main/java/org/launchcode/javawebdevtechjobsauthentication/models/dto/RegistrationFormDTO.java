package org.launchcode.javawebdevtechjobsauthentication.models.dto;

public class RegistrationFormDTO extends org.launchcode.javawebdevtechjobsauthentication.models.dto.loginFormDTO {
    private String verifyPassword;

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }
}