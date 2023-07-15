package com.mygumi.insider.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@ApiModel(value = "가게 정보 DTO", description = "가게 정보 DTO")
public class StoreDTO {

    @ApiModelProperty(value = "가게 ID")
    private Long id;

    @ApiModelProperty(value = "가게 이름")
    private String storeName;

    @ApiModelProperty(value = "가게 종류")
    private String category;

    @ApiModelProperty(value = "가게 주소")
    private String address;

    @ApiModelProperty(value = "가게 위도")
    private String lat;

    @ApiModelProperty(value = "가게 경도")
    private String lng;

    @ApiModelProperty(value = "가게 사진")
    private String store_image;

    @ApiModelProperty(value = "가게 영업 시간")
    private String store_hours;
}
