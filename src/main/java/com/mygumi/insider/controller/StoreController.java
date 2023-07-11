package com.mygumi.insider.controller;

import com.mygumi.insider.Service.StoreService;
import com.mygumi.insider.dto.StoreDTO;
import com.mygumi.insider.dto.StoreWithReviewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeService.getAllStore();
    }

    @GetMapping("/{storeId}")
    public StoreWithReviewDTO getStoreWithReviews(@PathVariable("storeId") Long storeId) {

        return storeService.getStoreWithReviews(storeId);
    }
}
