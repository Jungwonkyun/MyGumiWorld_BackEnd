package com.mygumi.insider.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Getter @Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "메뉴 정보 DTO", description = "메뉴 정보 DTO")
public class MealDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "modifiedAt")
    private String modifiedAt;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "time")
    private String time;

    @ApiModelProperty(value = "메뉴")
    private String menu;

    @ApiModelProperty(value = "칼로리")
    private String kcal;

    @ApiModelProperty(value = "코스")
    private String course;

    @ApiModelProperty(value = "사진 URL")
    private String photoURL;

}
