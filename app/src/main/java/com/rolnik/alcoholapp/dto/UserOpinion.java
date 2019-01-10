package com.rolnik.alcoholapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@JsonPropertyOrder({"id", "saleId", "opinion"})
public class UserOpinion {
    @JsonIgnore
    public static final int LIKE = 1;
    @JsonIgnore
    public static final int DISLIKE = -1;

    private int id;
    private int saleId;
    private int opinion;

}
