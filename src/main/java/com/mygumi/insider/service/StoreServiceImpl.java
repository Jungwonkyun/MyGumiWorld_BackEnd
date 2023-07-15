package com.mygumi.insider.service;

import com.mygumi.insider.domain.Review;
import com.mygumi.insider.domain.Store;
import com.mygumi.insider.dto.StoreDTO;
import com.mygumi.insider.dto.StoreWithReviewDTO;
import com.mygumi.insider.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    @Override
    public List<StoreDTO> getAllStore() {

        Iterable<Store> findAllStores = storeRepository.findAll();

        List<StoreDTO> stores = new ArrayList<>();
        for (Store findStore : findAllStores) {

            StoreDTO tmp = StoreDTO.builder()
                    .id(findStore.getId())
                    .storeName(findStore.getStoreName())
                    .address(findStore.getAddress())
                    .lng(findStore.getLng())
                    .lat(findStore.getLat())
                    .category(findStore.getCategory())
                    .store_image(findStore.getStoreImage())
                    .store_hours(findStore.getStoreHours())
                    .build();

            stores.add(tmp);
        }

        return stores;
    }

    @Override
    public StoreWithReviewDTO getStoreWithReviews(Long storeId) {

        Store findStoreWithReviews = storeRepository.getStoreWithReviews(storeId);

        StoreWithReviewDTO storeWithReviewDTO = StoreWithReviewDTO.builder()
                .id(findStoreWithReviews.getId())
                .storeName(findStoreWithReviews.getStoreName())
                .address(findStoreWithReviews.getAddress())
                .lng(findStoreWithReviews.getLng())
                .lat(findStoreWithReviews.getLat())
                .category(findStoreWithReviews.getCategory())
                .build();

        storeWithReviewDTO.setReviews(new ArrayList<>());

        List<Review> findReviews = findStoreWithReviews.getReviews();
        System.out.println("==========================");
        System.out.println("findReviews.size() = " + findReviews.size());
        for (Review findReview : findReviews) {
//            System.out.println(findReview);
//            System.out.println(storeWithReviewDTO.getReviews());
            storeWithReviewDTO.getReviews().add(findReview);
        }

        storeWithReviewDTO.setAvgStar(storeRepository.getAverageStar(storeId));

        return storeWithReviewDTO;
    }

    @Override
    public StoreWithReviewDTO getStoreWithReviewsByStoreName(String storeName) {

        Store findStoreWithReviews = storeRepository.getStoreWithReviewsByStoreName(storeName);

        StoreWithReviewDTO storeWithReviewDTO = StoreWithReviewDTO.builder()
                .id(findStoreWithReviews.getId())
                .storeName(findStoreWithReviews.getStoreName())
                .address(findStoreWithReviews.getAddress())
                .lng(findStoreWithReviews.getLng())
                .lat(findStoreWithReviews.getLat())
                .category(findStoreWithReviews.getCategory())
                .build();

        storeWithReviewDTO.setReviews(new ArrayList<>());

        List<Review> findReviews = findStoreWithReviews.getReviews();
        System.out.println("==========================");
        System.out.println("findReviews.size() = " + findReviews.size());
        for (Review findReview : findReviews) {
//            System.out.println(findReview);
//            System.out.println(storeWithReviewDTO.getReviews());
            storeWithReviewDTO.getReviews().add(findReview);
        }

        storeWithReviewDTO.setAvgStar(storeRepository.getAverageStar(findStoreWithReviews.getId()));

        return storeWithReviewDTO;
    }
}
