package com.mygumi.insider.service;

import com.mygumi.insider.dto.StoreDTO;
import com.mygumi.insider.dto.StoreWithReviewDTO;

import java.util.List;

public interface StoreService {

    List<StoreDTO> getAllStore();

    List<StoreDTO> getStoreWithAutoComplete(String search);

    StoreWithReviewDTO getStoreWithReviews(Long storeId);

    StoreWithReviewDTO getStoreWithReviewsByStoreName(String storeName);
}
