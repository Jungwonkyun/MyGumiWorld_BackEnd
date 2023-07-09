package com.mygumi.insider.dto;

import java.util.ArrayList;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "댓글 Dto, status 1 일시 존재, 0 일시 삭제")
public class CommentDto {
	
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
