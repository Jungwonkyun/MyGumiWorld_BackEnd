package com.mygumi.insider.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Path {

    @Id
    @GeneratedValue
    private Long id;

    private Long commentId;

    private String imagePath;
}
