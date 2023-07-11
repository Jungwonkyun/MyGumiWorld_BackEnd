package com.mygumi.insider.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "게시물의 댓글 Dto")
public class CommentDto {

	@ApiModelProperty(value = "")
	private String commentNo;
	private String content;
	private String boardNo;
	private String writerId;
	private String writerName;
	private String createDate;
	private int status;
	private ArrayList<ReplyCommentDto> replyList;
	
	public CommentDto() {
		super();
		replyList = new ArrayList<ReplyCommentDto>();
	}
	
	public void addReply(ReplyCommentDto reply) {
		replyList.add(reply);
	}
	
}
