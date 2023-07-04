package com.mygumi.insider.controller;

import com.mygumi.insider.service.ReviewService;
import com.mygumi.insider.dto.ReviewDTO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ApiOperation(value = "리뷰 남기기", notes = "POST 방식으로 리뷰 남기기")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void LeaveTheReview(
            @RequestPart(value = "image", required = false) List<MultipartFile> files,
            @RequestPart(value = "content", required = false) ReviewDTO reviewDTO
    ) throws IOException {

        reviewService.leaveTheReview(files, reviewDTO);
    }

    @GetMapping(value = "/image/{filePath}/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(
            @PathVariable("filePath") String filePath,
            @PathVariable("fileName") String fileName
    ) throws IOException {

        String savePath = System.getProperty("user.dir") +
                File.separatorChar + "src" +
                File.separatorChar + "main" +
                File.separatorChar + "resources" +
                File.separatorChar + "static" + File.separatorChar + filePath + File.separatorChar + fileName;

        System.out.println("savePath = " + savePath);

        InputStream in = new FileInputStream(savePath);
        System.out.println(in);

        return IOUtils.toByteArray(in);
    }
}
