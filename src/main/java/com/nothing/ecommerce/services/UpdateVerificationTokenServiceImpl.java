package com.nothing.ecommerce.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nothing.ecommerce.entity.UpdateVerificationToken;
import com.nothing.ecommerce.entity.User;
import com.nothing.ecommerce.miscellaneous.EmailTemplate;
import com.nothing.ecommerce.miscellaneous.Miscellaneous;
import com.nothing.ecommerce.repository.UpdateVerificationTokenRepository;

@Service
public class UpdateVerificationTokenServiceImpl implements UpdateVerificationTokenService {

    private String verificationLink = "http://localhost:8080/users/verify-update?token=";
    private String applicationName = "Ecommerce_web";
    private String emailSubject = "Verify Your updated Email Address for " + applicationName;
    private int EXPIRATION = 1;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UpdateVerificationTokenRepository updateVerificationTokenRepository;
    @Autowired
    private UserService userService;

    @Override
    public void sender(String reference) {
        User user = userService.get(reference);
        UpdateVerificationToken updateVerificationToken = updateVerificationTokenRepository
                .findByUserId(user.getUserId());

        String email = user.getEmail();
        String token = updateVerificationToken.getToken();
        String verificationTemplate = EmailTemplate.EMAIL_UPDATE_VERIFICATION_TEMPLATE;
        String formatedMessage = String.format(verificationTemplate,
                user.getName(), applicationName,
                updateVerificationToken.getData(),
                verificationLink + token, verificationLink + token, EXPIRATION,
                applicationName,
                applicationName);

        try {
            emailService.sendEmail(email, emailSubject, formatedMessage);
        } catch (Exception e) {
            System.out.println("unable to send email:");
            e.printStackTrace();
        }
    }

    @Override
    public void sender(User user, UpdateVerificationToken updateVerificationToken) {
        String data = updateVerificationToken.getData();
        String email = null;
        if (Miscellaneous.isValidEmail(data)) {
            email = data;
        } else {
            email = user.getEmail();
        }

        String token = updateVerificationToken.getToken();
        String verificationTemplate = EmailTemplate.EMAIL_UPDATE_VERIFICATION_TEMPLATE;
        String formatedMessage;
        formatedMessage = String.format(verificationTemplate,
                user.getName(), applicationName,
                updateVerificationToken.getData(),
                verificationLink + token, verificationLink + token, EXPIRATION,
                applicationName,
                applicationName);

        try {
            emailService.sendEmail(email, emailSubject, formatedMessage);
        } catch (Exception e) {
            System.out.println("unable to send email");
            e.printStackTrace();
        }
    }

    @Override
    public UpdateVerificationToken findByToken(String token) {
        return updateVerificationTokenRepository.findByToken(token);
    }

    @Override
    public UpdateVerificationToken findByData(String data) {
        return updateVerificationTokenRepository.findByData(data);
    }

    @Override
    public UpdateVerificationToken save(UpdateVerificationToken token) {
        return updateVerificationTokenRepository.save(token);
    }

    @Override
    public void delete(UpdateVerificationToken token) {
        updateVerificationTokenRepository.delete(token);
    }

    @Override
    public boolean verify(String token) {
        // fetch token from Database
        UpdateVerificationToken updateVerificationToken = findByToken(token);
        // check if token exist and not expired
        if (updateVerificationToken != null && updateVerificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean verify(UpdateVerificationToken token) {
        // check if token exist and not expired
        if (token != null && token.getExpiryDate().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    @Override
    public UpdateVerificationToken generate(int userId, String data) {
        // check if data is not used before
        UpdateVerificationToken updateVerificationToken = findByData(data);
        if (updateVerificationToken != null) {
            delete(updateVerificationToken);
        }

        // Generate Verification Token
        String token = UUID.randomUUID().toString();

        updateVerificationToken = new UpdateVerificationToken(data, token, userId);

        return save(updateVerificationToken);
    }
}
