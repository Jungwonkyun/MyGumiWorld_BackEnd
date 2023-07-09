package com.mygumi.insider.controller;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/board")
@Api(value="Board 컨트롤러 API")
public class BoardController {

	private final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";

	private BoardService boardService;

	private final AuthTokensGenerator authTokensGenerator;
	
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
	public ResponseEntity<Map<String, Object>> getBoard(
			@ApiParam(value = "해당 유저의 JWT 토큰") @RequestHeader("Authorization") String jwt,
			@PathVariable("boardNo")int boardNo){
		logger.info("게시글 {}번 반환", boardNo);
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			//게시글 내용 반환
			BoardDto board = boardService.getBoardDetail(boardNo);
			resultMap.put("boardDetail", board);
			//댓글+답글 내용 반환
			List<CommentDto> comments = boardService.getBoardcomments(boardNo);
			resultMap.put("comments", comments);
			//좋아요 여부 반환

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
			@RequestHeader("Authorization") String jwt, @RequestPart(value="boardDto") BoardDto boardDto, @RequestPart(value = "files", required = false) MultipartFile[] files){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("게시물 작성");
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);
		boardDto.setWriterId(id);
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
			@RequestHeader("Authorization") String jwt, @RequestBody CommentDto commentDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("댓글 작성");
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);
		commentDto.setWriterId(id);
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
			@RequestHeader("Authorization") String jwt, @RequestBody ReplyCommentDto replyDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("대댓글 작성");
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);
		replyDto.setWriterId(id);
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
			@RequestHeader("Authorization") String jwt, @RequestBody BoardDto boardDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);
		logger.info("게시글 수정 작성");

		// 작성자가 아닌 사람이 수정하려 할 때
		if(boardDto.getWriterId() != id) {
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
			@RequestHeader("Authorization") String jwt, @RequestBody CommentDto commentDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		logger.info("댓글 수정 작성");
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);

		// 작성자가 아닌 사람이 수정하려 할 때
		if(commentDto.getWriterId()!=id) {
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
			@RequestHeader("Authorization") String jwt, @RequestBody ReplyCommentDto replyDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("대댓글 수정 작성");
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);
		// 작성자가 아닌 사람이 수정하려 할 때
		if(replyDto.getWriterId()!=id) {
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
			@RequestHeader("Authorization") String jwt, @RequestParam String boardNo, @RequestParam String BoardWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("게시글 삭제");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		// 작성자가 아닌 사람이 수정하려 할 때
		if(BoardWriterId.equals(id)) {
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
			@RequestHeader("Authorization") String jwt, @RequestParam String commentNo, @RequestParam String commentWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("댓글 삭제");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!commentWriterId.equals(id)) {
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
			@RequestHeader("Authorization") String jwt, @RequestParam String replyNo, @RequestParam String replyWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("대댓글 삭제");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!replyWriterId.equals(id)) {
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

	@ApiOperation(value = "좋아요 설정(좋아요)")
	@PutMapping("/like")
	public ResponseEntity<Map<String, Object>> likeBoard(
			@RequestHeader("Authorization") String jwt, @RequestParam String boardNo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("좋아요 설정");
		String accessToken = jwt.replace("Bearer ", "");
		long id = authTokensGenerator.extractMemberId(accessToken);

		// 좋아요 설정
		try {
			boardService.likeBoard(boardNo, id);
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
}
