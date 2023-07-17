package com.mygumi.insider.controller;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mygumi.insider.domain.oauth.AuthTokensGenerator;
import com.mygumi.insider.dto.*;
import com.mygumi.insider.service.MealService;
import io.swagger.annotations.ApiParam;
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

import com.mygumi.insider.service.BoardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = { "*" }, maxAge=600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/meal")
@Api(value="Meal 컨트롤러 API", description = "식사 메뉴 크롤링 Controller")
public class MealController {

    private final Logger logger = LoggerFactory.getLogger(BoardController.class);
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    @Autowired
    private MealService mealService;

    @ApiOperation(value = "식사 목록 반환")
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getLunch(){
        logger.info("식사 목록 반환");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            resultMap.put("data", mealService.getMeal());
            resultMap.put("message", SUCCESS);
            return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
        } catch (Exception e) {
            resultMap.put("message", FAIL);
            return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
        }
    }
}
