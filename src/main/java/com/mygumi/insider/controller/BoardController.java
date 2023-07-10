package com.mygumi.insider.controller;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mygumi.insider.dto.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mygumi.insider.dto.BoardDto;
import com.mygumi.insider.dto.CommentDto;
import com.mygumi.insider.dto.ReplyCommentDto;
import com.mygumi.insider.service.BoardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = { "*" }, maxAge=600)
@RestController
@RequestMapping("/board")
@Api(value="Board 컨트롤러 API")
public class BoardController {

	private final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	@Autowired
	private BoardService boardService;
	
	// 닉네임 join 필요
	@ApiOperation(value = "전체 게시판 반환(목록 형태)")
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getBoards(){
		logger.info("BoardList 모두 반환");
		List<BoardDto> boardList;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			boardList = boardService.getBoards();
			resultMap.put("boardList", boardList);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
	}
	
	// 닉네임 join 필요
	@ApiOperation(value = "게시물 상세보기 및 댓글+대댓글 반환")
	@GetMapping("/{boardNo}")
	public ResponseEntity<Map<String, Object>> getBoard(@PathVariable("boardNo")int boardNo){
		logger.info("게시글 {}번 반환", boardNo);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			//게시글 내용 반환
			BoardDto board = boardService.getBoardDetail(boardNo);
			resultMap.put("boardDetail", board);
			//댓글+답글 내용 반환
			List<CommentDto> comments = boardService.getBoardcomments(boardNo);
			resultMap.put("comments", comments);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "게시물 쓰기")
	@PostMapping("/writeBoard")
	public ResponseEntity<Map<String, Object>> writeBoard(
			@RequestPart(value="boardDto") BoardDto boardDto, @RequestPart(value = "files", required = false) MultipartFile[] files){
//	@ApiOperation(value = "게시물 쓰기")
//	@PostMapping("/writeBoard")
//	public ResponseEntity<Map<String, Object>> writeBoard(@RequestBody Map<String,Object> boardInfo){
//		
//		MultipartFile file = (MultipartFile) boardInfo.get("fileInfo");
//		BoardDto boardDto = (BoardDto) boardInfo.get("boardDtoInfo");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("게시물 작성");
		logger.debug("게시물 내용 {}:", boardDto);
		
		try {
			for(MultipartFile file : files) {
				String projectPath = new File("").getAbsolutePath();
				String fileSavePath = "/src/main/resources/static/files";
				String originName = file.getOriginalFilename();
				String saveName = UUID.randomUUID().toString() + originName.substring(originName.lastIndexOf('.'));
				boardDto.setFolder(projectPath+fileSavePath);
				boardDto.setOriginName(originName);
				boardDto.setSaveName(saveName);
				logger.debug("파일 저장 : {}", projectPath);
				file.transferTo(new File(projectPath+fileSavePath, saveName));
			}
			boardService.writeBoard(boardDto);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "댓글 작성")
	@PostMapping("/writeComment")
	public ResponseEntity<Map<String, Object>> writeComment(
			// @RequestParam String content, @RequestParam String boardNo, @RequestParam String writerId, @RequestParam String writerName){
			@RequestBody CommentDto commentDto){
			Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("댓글 작성");
		// CommentDto commentDto = new CommentDto();
		// commentDto.setContent(content);
		// commentDto.setWriterId(writerId);
		// commentDto.setWriterName(writerName);
		// commentDto.setBoardNo(boardNo);
		logger.debug("댓글 내용 {}:", commentDto);
		try {
			boardService.writeComment(commentDto);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "대댓글 작성")
	@PostMapping("/writeReply")
	public ResponseEntity<Map<String, Object>> writeReply(
		@RequestBody ReplyCommentDto replyDto){
			// @RequestParam String content, @RequestParam String boardNo, @RequestParam String commentNo,
			// @RequestParam String writerId, @RequestParam String writerName){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("대댓글 작성");
		// ReplyCommentDto replyDto = new ReplyCommentDto();
		// replyDto.setBoardNo(boardNo);
		// replyDto.setCommentNo(commentNo);
		// replyDto.setContent(content);
		// replyDto.setWriterId(writerId);
		// replyDto.setWriterName(writerName);
		logger.debug("대댓글 내용 {}:", replyDto);
		try {
			boardService.writeReply(replyDto);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "게시글 수정")
	@PutMapping("/modifyBoard")
	public ResponseEntity<Map<String, Object>> modifyBoard(
			@RequestBody BoardDto boardDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// jwt 받아서 수정
		String writerId = null;
		logger.info("게시글 수정 작성");
		if(boardDto.getWriterId() == null || writerId == null) {
			logger.info("실패 : null값 입력");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "null value");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);						
		}
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!boardDto.getWriterId().equals(writerId)) {
			logger.info("실패 : 다른 사람의 게시글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 게시글 수정
		try {
			boardService.modifyBoard(boardDto);
			resultMap.put("message", SUCCESS);
			logger.info("성공");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "댓글 수정")
	@PutMapping("/modifyComment")
	public ResponseEntity<Map<String, Object>> modifyComment(
			@RequestBody CommentDto commentDto){
		String writerId = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("댓글 수정 작성");
		if(commentDto.getWriterId() == null || writerId == null) {
			logger.info("실패 : null값 입력");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "null value");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);						
		}
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!commentDto.getWriterId().equals(writerId)) {
			logger.info("실패 : 다른 사람의 댓글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 댓글 수정
		try {
			boardService.modifyComment(commentDto);
			resultMap.put("message", SUCCESS);
			logger.info("성공");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "대댓글 수정")
	@PutMapping("/modifyReply")
	public ResponseEntity<Map<String, Object>> modifyReply(
			@RequestBody ReplyCommentDto replyDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String writerId = "";
		logger.info("대댓글 수정 작성");
		if(replyDto.getWriterId() == null || writerId == null) {
			logger.info("실패 : null값 입력");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "null value");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);						
		}
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!replyDto.getWriterId().equals(writerId)) {
			logger.info("실패 : 다른 사람의 대댓글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 대댓글 수정
		try {
			boardService.modifyReply(replyDto);
			resultMap.put("message", SUCCESS);
			logger.info("성공");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "게시글 삭제")
	@PutMapping("/deleteBoard")
	public ResponseEntity<Map<String, Object>> deleteBoard(
			@RequestParam String boardNo, @RequestParam String BoardWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String writerId = "";
		logger.info("게시글 삭제");
		if(BoardWriterId == null || writerId == null) {
			logger.info("실패 : null값 입력");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "null value");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);						
		}
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!BoardWriterId.equals(writerId)) {
			logger.info("실패 : 다른 사람의 게시글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 게시글 수정
		try {
			boardService.deleteBoard(boardNo);
			resultMap.put("message", SUCCESS);
			logger.info("성공");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "댓글 삭제")
	@PutMapping("/deleteComment")
	public ResponseEntity<Map<String, Object>> deleteComment(
			@RequestParam String commentNo, @RequestParam String commentWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String writerId = "";
		logger.info("댓글 삭제");
		if(commentWriterId == null || writerId == null) {
			logger.info("실패 : null값 입력");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "null value");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);						
		}
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!commentWriterId.equals(writerId)) {
			logger.info("실패 : 다른 사람의 댓글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 댓글 수정
		try {
			boardService.deleteComment(commentNo);
			resultMap.put("message", SUCCESS);
			logger.info("성공");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "대댓글 수정")
	@PutMapping("/deleteReply")
	public ResponseEntity<Map<String, Object>> deleteReply(
			@RequestParam String replyNo, @RequestParam String replyWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String writerId = "";
		logger.info("대댓글 삭제");
		if(replyWriterId == null || writerId == null) {
			logger.info("실패 : null값 입력");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "null value");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);						
		}
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!replyWriterId.equals(writerId)) {
			logger.info("실패 : 다른 사람의 대댓글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 대댓글 수정
		try {
			boardService.deleteReply(replyNo);
			resultMap.put("message", SUCCESS);
			logger.info("성공");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}


	@PostMapping("/report")
	public ResponseEntity<Map<String, Object>> report(@RequestBody Report report){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Report newReport =  boardService.report(report);
			resultMap.put("message", SUCCESS);
			resultMap.put("report", newReport);
			logger.info("성공");
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			logger.info("실패");

		}
		return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
	}
	
}
