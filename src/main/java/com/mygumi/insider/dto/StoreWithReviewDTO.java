package com.mygumi.insider.dto;

import com.mygumi.insider.domain.Review;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(value = "특정 가게 정보 불러오기", description = "가게 정보 + 댓글 정보")
public class StoreWithReviewDTO {

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

    @ApiModelProperty(value = "가게 평점")
    private int avgStar;

    @ApiModelProperty(value = "가게 후기들")
    private List<Review> reviews;
}
