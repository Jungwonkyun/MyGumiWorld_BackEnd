package com.mygumi.insider.controller;


import com.mygumi.insider.dto.Tip;
import com.mygumi.insider.repository.TipRepository;
import com.mygumi.insider.service.TipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tip")
@Api(value = "꿀팁 정보에 대한 Controller")
public class TipController {

    private final TipRepository tipRepository;
    private final TipService tipService;

    //전체 리스트 보기
    @ApiOperation(value = "팁에 대한 모든 정보를 보여주는 API")
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Tip>> list() throws Exception {
        return ResponseEntity.ok(tipRepository.findAll());
    }


    //특정 게시물 보기
    @ApiOperation(value = "특정 팁에 대한 상세보기 API",
                  notes = "카드뉴스 식으로 미리보기 만들어 놓고(제목만) 누르면 상세보기 보이는 식으로")
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Tip> view(@ApiParam(value = "팁 번호") @PathVariable(value = "id") int id) throws Exception {
        return ResponseEntity.ok(tipRepository.findById(id).orElse(null));
    }


    //게시물 따봉 올라가도록
    @ApiOperation(value = "해당 팁에 대한 따봉을 올려주는 API",
            notes = "따봉 버튼을 누르면 해당 ID에 대한 Hit값이 올라가도록")
    @GetMapping("/updateHit/{id}")
    @ResponseBody
    public ResponseEntity<?> updateHit(@ApiParam(value = "팁 번호") @PathVariable(value = "id") int id) throws Exception {
        return ResponseEntity.ok(tipService.updateHit(id));
    }

}
