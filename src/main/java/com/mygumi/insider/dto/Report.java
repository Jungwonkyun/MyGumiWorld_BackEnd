package com.mygumi.insider.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String report;

    private int boardNo;

    private String reportedId;

    @Builder
    public Report(int id, String report, int boardNo, String reportedId) {
        this.id = id;
        this.report = report;
        this.boardNo = boardNo;
        this.reportedId = reportedId;
    }
}
