package com.example.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeCheckoutService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${app.base-url}")
    private String appBaseUrl;

    public Session createPremiumCheckoutSession(Integer memberId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(appBaseUrl + "/members/club/upgrade/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(appBaseUrl + "/members/club/upgrade/cancel")
                .putMetadata("memberId", String.valueOf(memberId))
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("jpy")
                                                .setUnitAmount(500L)
                                                .setRecurring(
                                                        SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                                                .setInterval(
                                                                        SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                                                .build())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("MyReserve プレミアム会員")
                                                                .build())
                                                .build())
                                .build())
                .build();

        return Session.create(params);
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        return Session.retrieve(sessionId);
    }
}