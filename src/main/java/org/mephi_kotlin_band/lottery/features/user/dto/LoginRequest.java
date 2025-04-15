package org.mephi_kotlin_band.lottery.features.user.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class LoginRequest {
    private String username;
    private String password;
}