package com.mygumi.insider.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mygumi.insider.service.WeatherService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = { "*" }, maxAge = 600)
@RestController
@PropertySource("classpath:application.yml")
@RequestMapping("/weather")
@Api(value = "날씨 컨트롤러 API")
public class WeatherController {

	private final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	@Autowired
	private WeatherService weatherService;

	@ApiOperation(value = "구미 인동 날씨 가져오기")
	@GetMapping("/")
	public ResponseEntity<Map<String, Object>> getWeather() {
		logger.info("날씨 출력");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("data", weatherService.getWeather());
			resultMap.put("message", SUCCESS);
			return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
		} catch (Exception e) {
			resultMap.put("message", FAIL);
			return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
		}
	}

}
