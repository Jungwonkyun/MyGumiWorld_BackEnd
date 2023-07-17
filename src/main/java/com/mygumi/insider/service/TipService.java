package com.mygumi.insider.service;


import com.mygumi.insider.dto.Tip;
import com.mygumi.insider.repository.TipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TipService {

    private final TipRepository tipRepository;

    public TipService(TipRepository tipRepository) {
        this.tipRepository = tipRepository;
    }

    @Transactional
    public Object updateHit(int id) {
        Optional<Tip> oTip = tipRepository.findById(id);
        if(oTip.isEmpty()) return 0;

        Tip tip = oTip.get();
        tip.setGood(tip.getGood()+1);
        return tip.getGood()+1;
    }

}
