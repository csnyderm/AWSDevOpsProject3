package com.skillstorm.skillvestgateway.models;

import java.util.Objects;

public class UserModel {

    private String given_name;

    private String family_name;
    private String email;

    public UserModel() {
    }

    public UserModel(String given_name, String family_name, String email) {
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(given_name, userModel.given_name) && Objects.equals(family_name, userModel.family_name) && Objects.equals(email, userModel.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(given_name, family_name, email);
    }
}
