package cest.la.vie.service;

import cest.la.vie.persistence.UserRepository;
import cest.la.vie.persistence.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthenticationService {

    public final UserRepository userRepository;
    public final HashCalculator hashCalculator;

    public AuthenticationService(UserRepository userRepository, HashCalculator hashCalculator) {
        this.userRepository = userRepository;
        this.hashCalculator = hashCalculator;
    }

    public User takeUserfromLoginRequest(String email, String phoneNumber) throws IllegalArgumentException{

        User user=null;
        // Trova l'utente dall'email o dal il numero di telefono
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user = userRepository.findByPhone(phoneNumber).get();
        }{
            user = userRepository.findByEmail(email).get();
        }
        return user;

    }

    public boolean verifyPassword(User user, String password){
        if((user.getPassword()).equals(hashCalculator.calculateHash(password))){
            return true;
        }
        return false;
    }

}
