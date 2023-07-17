package com.mygumi.insider.service;

import com.mygumi.insider.dto.MealDTO;
import com.mygumi.insider.repository.MealRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface MealService {
    List<MealDTO> getMeal() throws Exception;
}
