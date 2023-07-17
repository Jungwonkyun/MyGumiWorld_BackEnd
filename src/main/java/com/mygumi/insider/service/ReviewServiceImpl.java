package com.mygumi.insider.service;

import com.mygumi.insider.domain.Path;
import com.mygumi.insider.domain.Review;
import com.mygumi.insider.domain.Store;
import com.mygumi.insider.dto.ReviewDTO;
import com.mygumi.insider.repository.PathRepository;
import com.mygumi.insider.repository.ReviewRepository;
import com.mygumi.insider.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.io.File.separatorChar;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final String savePath = System.getProperty("user.dir") +
            separatorChar + "src" +
            separatorChar + "main" +
            separatorChar + "resources" +
            separatorChar + "static";

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final PathRepository pathRepository;

    @Override
    @Transactional
    public void leaveTheReview(List<MultipartFile> files, ReviewDTO reviewDTO) {

        Optional<Store> findStoreById = storeRepository.findById(reviewDTO.getStoreId());
        Store findStore = null;

        if (findStoreById.isEmpty()) {
            log.error("가게 정보 없음");
            return;
        }

        findStore = findStoreById.get();

        Review review = Review.builder()
                .store(findStore)
                .userId(reviewDTO.getUserId())
                .comment(reviewDTO.getComment())
                .star(reviewDTO.getStar())
                .build();

        reviewRepository.save(review);

        if (!isNull(files) && !files.get(0).isEmpty()) {

            String uuid = UUID.randomUUID().toString();

            for (MultipartFile multipartFile : files) {

                String fileName = multipartFile.getOriginalFilename();
                String path = savePath + separatorChar + uuid;

                log.info("이미지 저장 경로: {}", path);

                new File(path).mkdir();
                // 이미지 저장
                File file = new File(path, fileName);

                try {
                    multipartFile.transferTo(file);
                } catch (IOException e) {
                    log.error("사진 저장 실패");
                    log.error(e.getMessage());
                }

                Path savePath = Path.builder()
                        .imagePath(uuid.concat("/").concat(fileName))
                        .reviewId(review.getId())
                        .build();

                pathRepository.save(savePath);
            }
        }
    }
}
