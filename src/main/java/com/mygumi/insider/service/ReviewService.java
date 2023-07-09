package com.mygumi.insider.service;

import com.mygumi.insider.dto.ReviewDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    void leaveTheReview(List<MultipartFile> files, ReviewDTO reviewDTO);
}
