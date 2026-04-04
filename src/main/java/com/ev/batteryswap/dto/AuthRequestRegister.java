package com.ev.batteryswap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestRegister {
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String confirmPassword;

    public String toString() {
        return "Authentication Request Register: " +
                "fullname: " + fullName +
                ", username: " + username +
                ", email: " + email;
    }
}
