package com.mygumi.insider.dto;

import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Tip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String subject;

    private String discription;

    private int good;

    @Builder
    public Tip(int id, String subject, String discription, int good) {
        this.id = id;
        this.subject = subject;
        this.discription = discription;
        this.good = good;
    }

    @Override
    public String toString(){
        return this.id+" "+this.subject+" "+this.discription+" "+this.good;
    }
}

