package com.mygumi.insider.controller;

import com.mygumi.insider.dto.DetailStoreDTO;
import com.mygumi.insider.mapper.StoreMapper;
import com.mygumi.insider.service.StoreService;
import com.mygumi.insider.dto.StoreDTO;
import com.mygumi.insider.dto.StoreWithReviewDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Api(value="가제 정보 API")
public class StoreController {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @ApiOperation(value = "DB에 있는 모든 가게 정보 반환")
    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeService.getAllStore();
    }

    @ApiOperation(value = "가게 이름으로 자동 완성 검색")
    @GetMapping("/q/{search}")
    public List<StoreDTO> getStoresByNameWithAutoComplete(@PathVariable("search") String search) {
        return storeService.getStoreWithAutoComplete(search);
    }

    @ApiOperation(value = "가게 ID로 가게 정보 검색")
    @GetMapping("/id/{storeId}")
    public DetailStoreDTO getDetailStoreInfo(@PathVariable("storeId") Long storeId) {

        DetailStoreDTO result = storeMapper.getDetailStoreInfo(storeId);

        if (result.getReviews() == null) {
            log.info("{}에 리뷰가 아직 없습니다!", result.getStore_name());
            result.setReviews(null);
        }

        return result;
    }

    @ApiOperation(
            value = "가게 이름으로 가게 정보 검색",
            notes = "가게 이름이 한글일 경우, EndPoint 마지막에 / <- 꼭 붙여주세요!!! \n" +
                    "안 그러면, Decoding 과정에서 문제 발생합니다." +
                    "예시) /store/스타벅스 구미인동점/"
    )
    @GetMapping("/{storeName}")
    public DetailStoreDTO getStoreWithReviews(@PathVariable("storeName") String storeName) {

        DetailStoreDTO result = storeMapper.getDetailStoreInfoByStoreName(storeName);

        if (result.getReviews() == null) {
            log.info("{}에 리뷰가 아직 없습니다!", result.getStore_name());
            result.setReviews(null);
        }

        return result;
    }
}
