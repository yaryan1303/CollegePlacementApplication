package com.college.PlacementApl.Service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioNumber;

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    @PostConstruct
    public void init() {
        try {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } catch (Exception e) {
            log.error("Twilio initialization failed", e);
            throw new RuntimeException("Twilio initialization failed", e);
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void sendSMS(String to, String message) {
        try {
            String formattedNumber = validateAndFormatNumber(to);
            Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(twilioNumber),
                    message
            ).create();
            log.info("SMS sent successfully to {}", formattedNumber);
        } catch (ApiException e) {
            log.error("Twilio API error while sending SMS to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS via Twilio");
        } catch (IllegalArgumentException e) {
            log.error("Invalid phone number: {}", to, e);
            throw e;
        }
    }

    private String validateAndFormatNumber(String number) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "IN"); // Change country if needed
            if (!phoneUtil.isValidNumber(phoneNumber)) {
                throw new IllegalArgumentException("Invalid phone number");
            }
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number format: " + number, e);
        }
    }
}
