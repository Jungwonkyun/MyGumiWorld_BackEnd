package com.mygumi.insider.service;

import java.util.List;

import com.mygumi.insider.dto.BoardDto;
import com.mygumi.insider.dto.CommentDto;
import com.mygumi.insider.dto.ReplyCommentDto;
import com.mygumi.insider.dto.Report;

public interface BoardService {

	List<BoardDto> getBoards() throws Exception;

	BoardDto getBoardDetail(int boardNo) throws Exception;

	List<CommentDto> getBoardcomments(int boardNo) throws Exception;

	void writeBoard(BoardDto boardDto) throws Exception;

	void writeComment(CommentDto commentDto) throws Exception;

	void writeReply(ReplyCommentDto replyDto) throws Exception;

	void modifyBoard(BoardDto boardDto) throws Exception;

	void modifyComment(CommentDto commentDto) throws Exception;

	void modifyReply(ReplyCommentDto replyDto) throws Exception;

	void deleteBoard(String boardNo) throws Exception;

	void deleteComment(String commentNo) throws Exception;

	void deleteReply(String replyNo) throws Exception;

	Report report(Report reportDto)throws Exception;

}
