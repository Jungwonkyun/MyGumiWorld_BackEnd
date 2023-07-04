package com.mygumi.insider.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "리뷰 정보 DTO", description = "리뷰 정보 DTO")
public class ReviewDTO {

    @ApiModelProperty(value = "가게 ID")
    private Long storeId;

    @ApiModelProperty(value = "유저 ID")
    private String userId;

    @ApiModelProperty(value = "가게 평점")
    private int star;

    @ApiModelProperty(value = "가게 후기")
    private String comment;
}
