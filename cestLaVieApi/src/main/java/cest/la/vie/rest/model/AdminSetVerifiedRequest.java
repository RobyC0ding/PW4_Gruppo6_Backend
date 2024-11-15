package cest.la.vie.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminSetVerifiedRequest {

    @JsonProperty("phone_number")
    private String phone_number;
    @JsonProperty("verified")
    private boolean verified;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "AdminSetVerifiedRequest{" +
                "phone_number='" + phone_number + '\'' +
                ", verified=" + verified +
                '}';
    }
}
