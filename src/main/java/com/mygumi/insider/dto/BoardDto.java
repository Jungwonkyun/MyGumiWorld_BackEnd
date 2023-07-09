package com.mygumi.insider.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "BoardDto : 게시판 정보")
public class BoardDto {
	private String boardNo;
	private String title;
	private String content;
	private int hit;
	private String createDate;
	private String updateDate;
	private long writerId;
	private String writerName;
	private int likesNum;
	// 해당 아이디 유저가 좋아요 눌렀는지 여부
	private int likeStatus;

	// 파일
	private String folder;
	private String originName;
	private String saveName;
	
}
