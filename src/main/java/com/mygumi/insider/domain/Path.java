package com.mygumi.insider.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Path {

    @Id
    @GeneratedValue
    @Column(name = "path_id")
    private Long id;

    private Long reviewId;

    private String imagePath;
}
