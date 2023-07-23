package com.mygumi.insider.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {

	private final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
	@Value("${weather-key}")
	private String weatherKey;
	private LocalDate nowDate;
	private LocalTime nowTime;
	private String Date;
	private String Hour; // 30분 단위
	private DateTimeFormatter formatDay = DateTimeFormatter.ofPattern("yyyyMMdd");
	private DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("HH");
	private DateTimeFormatter formatMin = DateTimeFormatter.ofPattern("mm");

	@Override
	public Map<String, Object> getWeather() throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		nowDate = LocalDate.now();
		nowTime = LocalTime.now();
		Date = nowDate.format(formatDay);
		int min = Integer.parseInt(nowTime.format(formatMin));
		int hour = Integer.parseInt(nowTime.format(formatHour));
		String minString;
		if (min > 40) {
			minString = "00";
		} else {
			minString = "00";
			hour = hour - 1;
			if (hour == -1) {
				Date = nowDate.minusDays(1).format(formatDay);
				hour = 23;
			}
		}
		String hourString = Integer.toString(hour);
		if (hourString.length() == 1)
			hourString = "0" + hourString;
		Hour = hourString + minString;
		logger.info("현재 날짜:{}", Date);
		logger.info("현재 시각:{}", Hour);

		StringBuilder urlBuilder = new StringBuilder(
				"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /* URL */
		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "="
				+ URLEncoder.encode(weatherKey, "UTF-8")); /* Service Key */
		urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*
																												 * 페이지번호
																												 */
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*
																													 * 한
																													 * 페이지
																													 * 결과
																													 * 수
																													 */
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*
																													 * 요청자료형식
																													 * (
																													 * XML
																													 * /
																													 * JSON)
																													 * Default:
																													 * XML
																													 */
		urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(Date, "UTF-8")); /*
																													 * ‘21년
																													 * 6월
																													 * 28일
																													 * 발표
																													 */
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(Hour, "UTF-8")); /*
																													 * 06시
																													 * 발표
																													 * (
																													 * 정시단위)
																													 */
		urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("86", "UTF-8")); /*
																											 * 예보지점의 X
																											 * 좌표값
																											 */
		urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("95", "UTF-8")); /*
																											 * 예보지점의 Y
																											 * 좌표값
																											 */
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());

		BufferedReader rd;
		// 요청이 성공적으로 오지 않았을 때, 예외처리
		if (conn.getResponseCode() != 200) {
			logger.debug("요청 실패");
			throw new Exception();
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		}
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		System.out.println(sb.toString());

		// Json Object로 입력 받기
		JSONObject jObject = new JSONObject(sb.toString());
		// 첫번째 Json 가져와서 읽기
		JSONObject weather = jObject.getJSONObject("response").getJSONObject("body").getJSONObject("items");
		JSONArray items = weather.getJSONArray("item");
		System.out.println(items);

		JSONObject item;
		String category;
		for (int i = 0; i < items.length(); i++) {
			item = items.getJSONObject(i);
			category = item.getString("category");
			// 강수 형태
			if (category.equals("PTY")) {
				int code = item.getInt("obsrValue");
				if (code == 0) {
					resultMap.put("강수 형태", "없음");
				} else if (code == 1) {
					resultMap.put("강수 형태", "비");
				} else if (code == 2) {
					resultMap.put("강수 형태", "비/눈");
				} else if (code == 3) {
					resultMap.put("강수 형태", "눈");
				} else if (code == 5) {
					resultMap.put("강수 형태", "빗방울");
				} else if (code == 6) {
					resultMap.put("강수 형태", "빗방울눈날림");
				} else if (code == 7) {
					resultMap.put("강수 형태", "눈날림");
				}
				continue;
			}
			// 1시간 강수량/mm
			if (category.equals("RN1")) {
				resultMap.put("1시간 강수량", item.getString("obsrValue"));
				continue;
			}
			// 기온/섭도
			if (category.equals("T1H")) {
				resultMap.put("온도", item.getString("obsrValue"));
				continue;
			}
		}

		return resultMap;
	}

}
