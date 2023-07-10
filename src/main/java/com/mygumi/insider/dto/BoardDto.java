package com.mygumi.insider.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "BoardDto : 게시판 정보")
public class BoardDto {

	@ApiModelProperty(value = "게시물 번호")
	private String boardNo;

	@ApiModelProperty(value = "제목")
	private String title;

	@ApiModelProperty(value = "내용")
	private String content;

	@ApiModelProperty(value = "조회수")
	private int hit;

	@ApiModelProperty(value = "작성 날짜")
	private String createDate;

	@ApiModelProperty(value = "수정 날짜")
	private String updateDate;

	@ApiModelProperty(value = "작성자 아이디(번호)")
	private String writerId;

	@ApiModelProperty(value = "작성자 닉네임")
	private String writerName;

	@ApiModelProperty(value = "좋아요 갯수")
	private int likesNum;

	@ApiModelProperty(value = "현재 접속한 유저가 해당 게시물에 좋아요를 눌렀는지 여부." +
			"0이면 누르지 않음, 1이면 좋아요 눌린 상태")
	private int likeStatus;

	// 파일
	@ApiModelProperty(value = "파일이 저장된 경로")
	private String folder;

	@ApiModelProperty(value = "파일의 원래 이름(작성자가 업로드 시, 등록한 이름")
	private String originName;

	@ApiModelProperty(value = "서버 내에 저장된 파일의 이름")
	private String saveName;

	@Override
	public String toString() {
		return "BoardDto{" +
				"boardNo='" + boardNo + '\'' +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				", hit=" + hit +
				", createDate='" + createDate + '\'' +
				", updateDate='" + updateDate + '\'' +
				", writerId='" + writerId + '\'' +
				", writerName='" + writerName + '\'' +
				", likesNum=" + likesNum +
				", likeStatus=" + likeStatus +
				", folder='" + folder + '\'' +
				", originName='" + originName + '\'' +
				", saveName='" + saveName + '\'' +
				'}';
	}
}
