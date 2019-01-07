package com.rolnik.alcoholapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {
    private static String BAD_CREDENTIALS = "Bad credentials";
    private static String USER_NOT_ACTIVATED = "User is disabled";

    @JsonProperty("error")
    private String error;


    public boolean isBadCredentialsError(){
        return error.equals(BAD_CREDENTIALS);
    }

    public boolean isUserNotActivatedError(){
        return error.equals(USER_NOT_ACTIVATED);
    }
}
