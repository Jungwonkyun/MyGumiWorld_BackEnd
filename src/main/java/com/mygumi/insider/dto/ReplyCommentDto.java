package com.mygumi.insider.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReplyCommentDto {
	
	private String replyNo;
	private String commentNo;
	private String boardNo;
	private String writerId;
	private String writerName;
	private String createDate;
	private String content;
	private int status;
	
	@Override
	public String toString() {
		return "ReplyCommentDto [replyNo=" + replyNo + ", commentNo=" + commentNo + ", boardNo=" + boardNo
				+ ", writerId=" + writerId + ", writerName=" + writerName + ", createDate=" + createDate + ", content="
				+ content + ", status=" + status + "]";
	}
	
}
