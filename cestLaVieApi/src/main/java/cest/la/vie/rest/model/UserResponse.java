package cest.la.vie.rest.model;

public class UserResponse {

    private String email;
    private String phoneNumber;
    private boolean verified;  // Stato di verifica dell'utente

    // Costruttore
    public UserResponse(String email, String phoneNumber, boolean verified) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.verified = verified;
    }

    // Getter e Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
