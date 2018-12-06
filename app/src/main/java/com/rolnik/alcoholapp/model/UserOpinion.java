package com.rolnik.alcoholapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@JsonPropertyOrder({"id", "saleId", "opinion"})
public class UserOpinion {
    @JsonIgnore
    public static final int LIKE = 1;
    @JsonIgnore
    public static final int DISLIKE = -1;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int saleId;
    private int opinion;

}
