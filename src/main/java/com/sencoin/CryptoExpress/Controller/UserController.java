package com.sencoin.CryptoExpress.Controller;

import com.sencoin.CryptoExpress.Dto.EmailDTO;
import com.sencoin.CryptoExpress.Dto.UserDTO;
import com.sencoin.CryptoExpress.Entities.User;
import com.sencoin.CryptoExpress.Service.OptService;
import com.sencoin.CryptoExpress.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/users")
public class UserController {



    @Autowired
    private UserService userService;

    @Autowired
    private OptService otpService;

    // Nouvel endpoint pour saisir l'adresse e-mail
    @PostMapping(path = "enter-email",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> enterEmail(@Validated @RequestBody String email) {
        System.out.println(email);

            // Vérifie si l'adresse e-mail existe déjà dans la base de données
            if (userService.emailExists(email)) {
                return ResponseEntity.badRequest().body("L'adresse e-mail existe déjà.");
            }

            // Enregistre l'adresse e-mail dans la base de données
            User user = new User();

            user.setEmail(email);
            userService.registerUser(user);

            // Génère et envoie l'OTP lors de la saisie de l'adresse e-mail
            String otp = otpService.generateAndSendOtp(email);

            return ResponseEntity.ok("Adresse e-mail enregistrée. Code OTP envoyé par e-mail.");


        }
    @GetMapping("/toto")
    public ResponseEntity<String> showAddDeclarantForm() {
        System.out.println("-----------------------------------------tutuddfkwdfmk-----------");
        return ResponseEntity.ok("Adresse e-mail enregistrée. Code OTP envoyé par e-mail.");
    }
    // Endpoint existant pour l'inscription
    /*@PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        // Logique d'enregistrement de l'utilisateur
        userService.registerUser(userDTO.toUser());

        // Continuer avec la vérification de l'OTP

        return ResponseEntity.ok("Utilisateur enregistré. Code OTP envoyé par e-mail.");
    }*/
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        // Vérifie si le mot de passe et la confirmation du mot de passe correspondent
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Le mot de passe et la confirmation du mot de passe ne correspondent pas.");
        }

        // Si les mots de passe correspondent, continuez avec l'inscription
        userService.registerUser(userDTO.toUser());

        return ResponseEntity.ok("Utilisateur enregistré. Code OTP envoyé par e-mail.");
    }


    // Endpoint existant pour la vérification de l'OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String userInputOtp) {
        // Obtient l'OTP stocké en fonction de l'e-mail de l'utilisateur
        String storedOtp = userService.getStoredOtpByEmail(email);

        // Vérifie l'OTP fourni par l'utilisateur
        boolean isOtpValid = otpService.verifyOtp(userInputOtp, storedOtp);

        if (isOtpValid) {
            // Marque l'utilisateur comme vérifié ou effectue toute autre action nécessaire
            userService.verifyUser(email);
            return ResponseEntity.ok("L'OTP est valide. Utilisateur vérifié.");
        } else {
            return ResponseEntity.badRequest().body("L'OTP est invalide.");
        }
    }
}
