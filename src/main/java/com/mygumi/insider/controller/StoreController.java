package com.mygumi.insider.controller;

import com.mygumi.insider.dto.DetailStoreDTO;
import com.mygumi.insider.mapper.StoreMapper;
import com.mygumi.insider.service.StoreService;
import com.mygumi.insider.dto.StoreDTO;
import com.mygumi.insider.dto.StoreWithReviewDTO;
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
public class StoreController {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeService.getAllStore();
    }

    @GetMapping("/id/{storeId}")
    public DetailStoreDTO getDetailStoreInfo(@PathVariable("storeId") Long storeId) {

        DetailStoreDTO result = storeMapper.getDetailStoreInfo(storeId);

        if (result.getReviews() == null) {
            log.info("{}에 리뷰가 아직 없습니다!", result.getStore_name());
            result.setReviews(null);
        }

        return result;
    }

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
