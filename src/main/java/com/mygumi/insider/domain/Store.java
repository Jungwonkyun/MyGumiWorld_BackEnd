package com.mygumi.insider.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String storeName;

    private String category;

    private String address;

    private String lat;

    private String lng;

    @JsonManagedReference
    @OneToMany(mappedBy = "store")
    private List<Review> reviews;
}
