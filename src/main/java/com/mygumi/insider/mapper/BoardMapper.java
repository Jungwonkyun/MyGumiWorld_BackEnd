package com.mygumi.insider.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mygumi.insider.dto.BoardDto;
import com.mygumi.insider.dto.CommentDto;
import com.mygumi.insider.dto.ReplyCommentDto;

@Mapper
public interface BoardMapper {

	public List<BoardDto> getBoards() throws SQLException;

	public BoardDto getBoardDetail(String boardNo) throws SQLException;

	public List<CommentDto> getComments(String boardNo) throws SQLException;

	public List<ReplyCommentDto> getReplys(String boardNo) throws SQLException;

	public void writeBoard(BoardDto boardDto) throws SQLException;

	public void writeComment(CommentDto commentDto) throws SQLException;

	public void writeReply(ReplyCommentDto replyDto) throws SQLException;

	public void modifyBoard(BoardDto boardDto) throws SQLException;

	public void modifyComment(CommentDto commentDto) throws SQLException;

	public void modifyReply(ReplyCommentDto replyDto) throws SQLException;

	public void deleteBoard(String boardNo) throws SQLException;

	public void deleteComment(String commentNo) throws SQLException;

	public void deleteReply(String replyNo) throws SQLException;

	public void updateHit(String boardNo) throws SQLException;
	
	public long getLikeStatus(String boardNo, String userId) throws SQLException;

	void likeBoard(String boardNo, String id) throws SQLException;

	void dislikeBoard(String boardNo, String id) throws SQLException;

	List<BoardDto> getMyBoards(String id) throws SQLException;

	List<BoardDto> getMyCommentBoards(String id) throws SQLException;

	List<BoardDto> getMyLikeBoards(String id) throws SQLException;
}
