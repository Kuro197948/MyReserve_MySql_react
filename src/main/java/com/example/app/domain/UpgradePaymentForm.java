package com.example.app.domain;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpgradePaymentForm {

    @NotBlank(message = "支払い方法を選択してください")
    private String paymentMethod;

    @NotBlank(message = "カード名義を入力してください")
    private String cardName;

    @NotBlank(message = "カード番号を入力してください")
    private String cardNumber;

    @NotBlank(message = "有効期限を入力してください")
    private String expiry;

    @NotBlank(message = "セキュリティコードを入力してください")
    private String cvc;
}