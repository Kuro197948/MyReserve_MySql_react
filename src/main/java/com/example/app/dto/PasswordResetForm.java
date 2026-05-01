package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PasswordResetForm {

    @NotBlank
    private String token;

    @NotBlank(message = "パスワードを入力してください")
    private String password;

    @NotBlank(message = "確認用パスワードを入力してください")
    private String confirmPassword;
}