package com.mygumi.insider.mapper;

import com.mygumi.insider.dto.DetailStoreDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StoreMapper {

    DetailStoreDTO getDetailStoreInfo();
}
