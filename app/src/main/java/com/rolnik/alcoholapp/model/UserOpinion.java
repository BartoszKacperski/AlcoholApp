package com.rolnik.alcoholapp.model;

import android.arch.persistence.room.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity(primaryKeys = "id")
@JsonPropertyOrder({"id", "user", "sale", "opinion"})
public class UserOpinion {
    @JsonIgnore
    private static final int LIKE = 1;
    @JsonIgnore
    private static final int DISLIKE = -1;

    private int id;
    private Sale sale;
    private int opinion;

}
