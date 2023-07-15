package com.mygumi.insider.controller;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mygumi.insider.dto.BoardDto;
import com.mygumi.insider.dto.CommentDto;
import com.mygumi.insider.dto.ReplyCommentDto;
import com.mygumi.insider.service.BoardService;

@CrossOrigin(origins = { "*" }, maxAge=600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
@Api(value="Board 컨트롤러 API", description = "기본 반환값은 Map 형태, 'message'='success'일 시 요청 성공, 'message'='fail' 일 시 요청 실패")
public class BoardController {

	private final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	private static final String URL1 = "https://proxy.goorm.io/service/64a522416321182637a86990_d3EDtUkpmSQtVTvRKp4.run.goorm.io/9080/file/load/";
	private static final String URL2 = "path=d29ya3NwYWNlJTJGTXlHdW1pV29ybGRfQmFja0VuZCUyRnNyYyUyRm1haW4lMkZyZXNvdXJjZXMlMkZzdGF0aWMlMkZmaWxlcyUyRmQ0YWE2ZThjLWEwZTAtNGRlNi04YzhjLTZiNjQzOTNiODE2YS5wbmc=&docker_id=d3EDtUkpmSQtVTvRKp4&secure_session_id=tSqBga4uiZ7LH5w3yaI1VsAsZbXVep6i";

	@Autowired
	private BoardService boardService;

	private final AuthTokensGenerator authTokensGenerator;

	@ApiOperation(value = "전체 게시판 반환(목록 형태)",
			notes = "boardList 배열 내에 각 게시물의 정보를 담아 반환." +
					"반환되는 정보 : boardNo, title, content(50자만 반환), hit, createDate, folder(해당 게시물의 사진 url), originName, saveName")
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

	@ApiOperation(value = "게시물 상세보기",
			notes="boardDetail : 게시물의 상세 내용/맵 형식. folder : 해당 게시물 내 사진의 url 주소, comments : 댓글과 대댓글이 이중 배열 형식으로 들어있음.")
	@GetMapping("/{boardNo}")
	public ResponseEntity<Map<String, Object>> getBoard(
			@ApiParam(value = "해당 유저의 JWT 토큰") @RequestHeader("Authorization") String jwt,
			@ApiParam(value = "조회할 게시물 번호") @PathVariable("boardNo")String boardNo){
		logger.info("게시글 {}번 반환", boardNo);
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			//게시글 내용 및 좋아요 수 반환
			System.out.println(boardNo);
			BoardDto board = boardService.getBoardDetail(boardNo);

			//댓글+답글 내용 반환
			List<CommentDto> comments = boardService.getBoardcomments(boardNo);

			//좋아요 여부 반환
			long likeStatus = boardService.getBoardLikeStatus(boardNo, id);
			if(likeStatus == 0){
				board.setLikeStatus(0);
			}else{
				board.setLikeStatus(1);
			}

			resultMap.put("boardDetail", board);
			resultMap.put("comments", comments);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
	}
	
	@ApiOperation(value = "게시물 쓰기",
			notes="한 게시물 당 사진은 하나만 저장, 1MB 이하만.\n" +
					"웬만하면 포스트맨으로 테스트 부탁드립니다,, 포스트맨 설정은 카톡 부탁드려요\n")
	@PostMapping("/writeBoard")
	public ResponseEntity<Map<String, Object>> writeBoard(
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "{\"title\":\"제목\", \"content\":\"내용\"}") @RequestPart(value="boardDto") BoardDto boardDto,
			@ApiParam(value = "files(배열 형태)") @RequestPart(value = "files", required = false) MultipartFile files){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("게시물 작성");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		boardDto.setWriterId(id);
		logger.debug("게시물 내용 {}:", boardDto);
		
		try {
			if(!files.isEmpty()){
//				logger.debug("null");
//			}
//			for(MultipartFile file : files) {
				logger.debug("파일 존재함");
				String projectPath = new File("").getAbsolutePath();
				String fileSavePath = "/src/main/resources/static/files";
				String originName = files.getOriginalFilename();
				String saveName = UUID.randomUUID().toString() + originName.substring(originName.lastIndexOf('.'));
				boardDto.setFolder(URL1 + saveName + URL2);
				boardDto.setOriginName(originName);
				boardDto.setSaveName(saveName);
				logger.debug("파일 저장 : {}", projectPath+fileSavePath);
				files.transferTo(new File(projectPath+fileSavePath, saveName));
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
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "{\"boardNo\":\"게시판번호\", \"content\":\"내용\"}") @RequestBody CommentDto commentDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("댓글 작성");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
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
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "{\"boardNo\":\"게시판번호\", \"commentNo\":\"대댓글이 달릴 댓글 번호\", \"content\":\"내용\"}")@RequestBody ReplyCommentDto replyDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("대댓글 작성");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
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
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "{\"boardNo\":\"게시판번호\", \"title\":\"새로운 제목\", \"content\":\"새로운 내용\", \"writerId\":\"해당 글을 작성했던 유저의 아이디\"}") @RequestPart(value="boardDto") BoardDto boardDto,
			@ApiParam(value = "files, 배열 형태") @RequestPart(value = "files", required = false) MultipartFile files){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		logger.info("게시글 수정 작성");

		// 작성자가 아닌 사람이 수정하려 할 때
		if(!boardDto.getWriterId().equals(id)) {
			logger.info("실패 : 다른 사람의 게시글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 게시글 수정
		try {
			// 사진이 있을 시, 사진 수정
			if(!files.isEmpty()){
//			for(MultipartFile file : files) {
				String projectPath = new File("").getAbsolutePath();
				String fileSavePath = "/src/main/resources/static/files";
				String originName = files.getOriginalFilename();
				String saveName = UUID.randomUUID().toString() + originName.substring(originName.lastIndexOf('.'));
				boardDto.setFolder(URL1+saveName+URL2);
				boardDto.setOriginName(originName);
				boardDto.setSaveName(saveName);
				logger.debug("파일 저장 : {}", projectPath+fileSavePath);
				files.transferTo(new File(projectPath+fileSavePath, saveName));
			}
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
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "{\"commentNo\":\"댓글 번호\", \"content\":\"댓글의 내용\", \"writerId\":\"원본 댓글의 작성자 아이디\"}") @RequestBody CommentDto commentDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		logger.info("댓글 수정 작성");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));

		// 작성자가 아닌 사람이 수정하려 할 때
		if(!commentDto.getWriterId().equals(id)) {
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
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "{\"replyNo\":\"대댓글 번호\", \"content\":\"대댓글의 내용\", \"writerId\":\"원본 대댓글의 작성자 아이디\"}") @RequestBody ReplyCommentDto replyDto){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("대댓글 수정 작성");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!replyDto.getWriterId().equals(id)) {
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
			@RequestHeader("Authorization") String jwt,
			@ApiParam(value = "게시물 번호") @RequestParam String boardNo,
			@ApiParam(value="게시물의 작성자 아이디") @RequestParam String BoardWriterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("게시글 삭제");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		// 작성자가 아닌 사람이 수정하려 할 때
		if(!BoardWriterId.equals(id)) {
			logger.info("실패 : 다른 사람의 게시글에 접근");
			resultMap.put("message", FAIL);
			resultMap.put("reason", "Wrong Writer");
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);			
		}
		
		// 게시글 삭제
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
	
	@ApiOperation(value = "대댓글 삭제")
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
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));

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

	@ApiOperation(value = "좋아요 삭제")
	@DeleteMapping("/dislike")
	public ResponseEntity<Map<String, Object>> dislikeBoard(
			@RequestHeader("Authorization") String jwt, @RequestParam String boardNo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("좋아요 해제");
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));

		// 좋아요 해제
		try {
			boardService.dislikeBoard(boardNo, id);
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

	@ApiOperation(value = "내가 쓴 글 목록 반환",
			notes = "boardList 배열 내에 각 게시물의 정보를 담아 반환." +
					"반환되는 정보 : boardNo, title, content(50자만 반환), hit, createDate, folder, originName, saveName")
	@GetMapping("/list/myboards")
	public ResponseEntity<Map<String, Object>> getMyBoards(
			@RequestHeader("Authorization") String jwt){
		logger.info("내가 쓴 글 목록 반환");
		List<BoardDto> boardList;
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			boardList = boardService.getMyBoards(id);
			resultMap.put("boardList", boardList);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
	}

	@ApiOperation(value = "내가 쓴 댓글이 포함된 글 목록 반환",
			notes = "boardList 배열 내에 각 게시물의 정보를 담아 반환." +
					"반환되는 정보 : boardNo, title, content(50자만 반환), hit, createDate, folder, originName, saveName")
	@GetMapping("/list/mycomments")
	public ResponseEntity<Map<String, Object>> getMyCommentBoards(
			@RequestHeader("Authorization") String jwt){
		logger.info("내가 쓴 글 목록 반환");
		List<BoardDto> boardList;
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			boardList = boardService.getMyCommentBoards(id);
			resultMap.put("boardList", boardList);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
	}

	@ApiOperation(value = "내가 좋아요 한 글 목록 반환",
			notes = "boardList 배열 내에 각 게시물의 정보를 담아 반환." +
					"반환되는 정보 : boardNo, title, content(50자만 반환), hit, createDate, folder, originName, saveName")
	@GetMapping("/list/likes")
	public ResponseEntity<Map<String, Object>> getMyLikeBoards(
			@RequestHeader("Authorization") String jwt){
		logger.info("내가 좋아요 한 글 목록 반환");
		List<BoardDto> boardList;
		String accessToken = jwt.replace("Bearer ", "");
		String id = String.valueOf(authTokensGenerator.extractMemberId(accessToken));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			boardList = boardService.getMyLikeBoards(id);
			resultMap.put("boardList", boardList);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
	}

	@ApiOperation(value = "최근 게시물 목록 3개",
			notes = "boardList 배열 내에 각 게시물의 정보를 담아 반환." +
					"반환되는 정보 : boardNo, title")
	@GetMapping("/list/new")
	public ResponseEntity<Map<String, Object>> getNewList(){
		logger.info("최신글 3개 반환");
		List<BoardDto> boardList;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			boardList = boardService.getNewList();
			resultMap.put("boardList", boardList);
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String,Object>>(resultMap, HttpStatus.OK);
		}
	}
}
