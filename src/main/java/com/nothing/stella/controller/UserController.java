package com.nothing.stella.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nothing.stella.exception.InvalidJWTHeaderException;
import com.nothing.stella.exception.UnknownErrorException;
import com.nothing.stella.exception.UserException;
import com.nothing.stella.model.UpdatePasswordRequest;
import com.nothing.stella.model.UserViewModel;
import com.nothing.stella.model.UserInputModel;
import com.nothing.stella.services.UserService;
import com.nothing.stella.services.JWTService;
import com.nothing.stella.services.UserAdvanceService;
import com.nothing.stella.services.VerificationTokenService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserAdvanceService userAdvanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private JWTService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserInputModel userModel) {
        try {
            if (userAdvanceService.register(userModel)) {
                return ResponseEntity.status(HttpStatus.OK).body("Success");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register");
            }
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unknown error: " + e.getMessage(), e);
            throw new UnknownErrorException("Error: unknown error");
        }
    }

    @PostMapping("/register/resend-token")
    public ResponseEntity<String> resendToken(@RequestBody String reference) {
        try {
            verificationTokenService.sender(reference);

            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unknown error: " + e.getMessage(), e);
            throw new UnknownErrorException("Error: unknown error");
        }
    }

    @PostMapping("/verify-user")
    public ResponseEntity<String> verify(@RequestParam String token) {
        boolean isVerified = userAdvanceService.verify(token);

        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
    }

    @PostMapping("/verify-update")
    public ResponseEntity<String> verifyUpdate(@RequestParam String token) {
        boolean isVerified = userAdvanceService.verifyUpdate(token);

        if (isVerified) {
            return ResponseEntity.ok("update verified successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
    }

    @GetMapping
    public UserViewModel getInfo(@RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return userService.getInfo(reference);
            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @PutMapping("/name")
    public UserViewModel updateName(@RequestBody String name, @RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return userAdvanceService.updateName(reference, name);
            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @PutMapping("/email")
    public ResponseEntity<String> updateEmail(@RequestBody String email,
            @RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                userAdvanceService.updateEmail(reference, email);

                return ResponseEntity.status(HttpStatus.OK).body("Success");

            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @PutMapping("/number")
    public ResponseEntity<String> updateNumber(@RequestBody String number,
            @RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                userAdvanceService.updateNumber(reference, number);

                return ResponseEntity.status(HttpStatus.OK).body("Success");

            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updateRequest,
            @RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                userAdvanceService.updatePassword(reference, updateRequest.getCurrentPassword(),
                        updateRequest.getNewPassword());

                return ResponseEntity.status(HttpStatus.OK).body("Success");

            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @PutMapping("/de-activate")
    public ResponseEntity<String> deactivate(@RequestBody String password,
            @RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                userAdvanceService.deactivate(reference, password);

                return ResponseEntity.status(HttpStatus.OK).body("Success");

            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    // @DeleteMapping
    // public ResponseEntity<String> delete(@RequestHeader("Authorization") String
    // jwtHeader) {
    // if (jwtService.verifyJwtHeader(jwtHeader)) {
    // // extract token from request header
    // String jwtToken = jwtHeader.substring(7);
    // try {
    // String reference = jwtService.fetchReference(jwtToken);
    // userAdvanceService.delete(reference);
    // return ResponseEntity.status(HttpStatus.OK).body("Success");
    // } catch (UserException e) {
    // throw e;
    // } catch (Exception e) {
    // logger.error("Unknown error: " + e.getMessage(), e);
    // throw new UnknownErrorException("Error: unknown error");
    // }
    // } else {
    // throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
    // }
    // }

    @GetMapping("/test")
    public int test(@RequestBody String ref) {
        try {
            return userService.findUserIdByReference(ref);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unknown error: " + e.getMessage(), e);
            throw new UnknownErrorException("Error: unknown error");
        }
    }

}
