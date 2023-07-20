package com.mygumi.insider.service;

import com.mygumi.insider.dto.MealDTO;
import com.mygumi.insider.repository.MealRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MealServiceImpl implements MealService {

    private final Logger logger = LoggerFactory.getLogger(MealServiceImpl.class);
    private final MealRepository mealRepository;

    public MealServiceImpl(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }
    @Value("${wellstory-key}")
    private String wellstoryKey;
    private LocalDate nowDate;
    private LocalTime nowTime;
    private String Date;
    private String Hour; // 30분 단위
    private DateTimeFormatter formatDay = DateTimeFormatter.ofPattern("yyyyMMdd");
    private DateTimeFormatter formatHour = DateTimeFormatter.ofPattern("HH");
    private DateTimeFormatter formatHourMin = DateTimeFormatter.ofPattern("HHmm");

    @Override
    public List<MealDTO> getMeal() throws Exception {
        MealDTO resultMap;
        List<MealDTO> resultMapList = new ArrayList<>();

        nowDate = LocalDate.now();
        nowTime = LocalTime.now();

        Date = nowDate.format(formatDay);
        int hour = Integer.parseInt(nowTime.format(formatHour))+9;
        if(hour > 23){
            Date = nowDate.plusDays(1).format(formatDay);
            hour = hour - 24;
        }
        // 생성 or 수정 시각 기록 후 분당 호출 1번으로 제한
        String now = nowTime.format(formatHourMin);


        Hour = Integer.toString(hour);
        logger.info("현재 날짜:{}", Date);
        logger.info("현재 시각:{}", Hour);

        int[] MealArray;
        String MealType;
        // 점심 [0, 1], 저녁 [0, 1, 5]
        if (hour >= 13) {
            // 저녁
            MealArray = new int[]{0, 1, 5};
            MealType = "3";
        }else {
            // 점심
            MealArray = new int[]{0, 1};
            MealType = "2";
        }
        String title = Date + MealType;

        // DB에서 조회 시도
        List<MealDTO> meallist = mealRepository.findByTitle(title);
        if (meallist.size() != 0) { // DB에 있을 때
            for (int i = 0; i <= 1; i++){
                if (((hour>= 11 && hour < 13) || hour >= 17) && meallist.get(i).getPhotoURL() == null
                    && !meallist.get(i).getModifiedAt().equals(now)){
                    updateDTO(Date, MealType, MealArray, title, now);
                    break;
                }
            }
            meallist = mealRepository.findByTitle(title);
            return meallist;
        }

        return mealRepository.saveAllAndFlush(callAPIList(Date, MealType, MealArray, title, now));
    }

    private void updateDTO(String Date, String MealType, int[] MealArray, String title, String now) throws Exception {
        List<MealDTO> NowMealList = callAPIList(Date, MealType, MealArray, title, now);
        List<MealDTO> meallist = mealRepository.findByTitle(title);
        for(int i = 0; i <= 1; i++){
            MealDTO NowMeal =  NowMealList.get(i); // api

            MealDTO meal = meallist.get(i); // DB
            MealDTO newmeal = mealRepository.findById(meal.getId()).get();

            newmeal.setPhotoURL(NowMeal.getPhotoURL());
            newmeal.setModifiedAt(now);

            mealRepository.save(newmeal);
        }
    }
    private List<MealDTO> callAPIList(String Date, String MealType, int[] MealArray, String title, String now) throws Exception {
        List<MealDTO> resultMapList = new ArrayList<>();

        // URL생성기
        StringBuilder urlBuilder =
                new StringBuilder("https://welplus.welstory.com/api/meal");
        urlBuilder.append("?" + URLEncoder.encode("menuDt", "UTF-8") + "="
                + URLEncoder.encode(Date, "UTF-8")); /* 오늘 날짜 YYYYMMDD */
        urlBuilder.append("&" + URLEncoder.encode("menuMealType", "UTF-8") + "="
                + URLEncoder.encode(MealType, "UTF-8")); /* 2는 점심 3은 저녁 */
        urlBuilder.append("&" + URLEncoder.encode("restaurantCode", "UTF-8") + "="
                + URLEncoder.encode("REST000213", "UTF-8")); /* 구미 식당 코드 == REST000213 */

        // URL호출
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Cookie", "remember-me=" + wellstoryKey);
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

        // Json Object로 입력 받기
        JSONObject jObject = new JSONObject(sb.toString());

        JSONObject data = jObject.getJSONObject("data");
        JSONArray mealList = data.getJSONArray("mealList");

        JSONObject item;
        for (int i : MealArray) {
            item = mealList.getJSONObject(i);

            String photo_url;
            if (item.isNull("photoCd")) photo_url = null;
            else photo_url = item.getString("photoUrl") + item.getString("photoCd");

            MealDTO meal = MealDTO.builder()
                    .title(title)
                    .modifiedAt(now)
                    .time(item.getString("menuMealTypeTxt"))
                    .menu(item.getString("subMenuTxt"))
                    .kcal(item.getString("kcal"))
                    .course(item.getString("courseTxt"))
                    .photoURL(photo_url)
                    .build();

            resultMapList.add(meal);
        }

        return resultMapList;
    }
}
