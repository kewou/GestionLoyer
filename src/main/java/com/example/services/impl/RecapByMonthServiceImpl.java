package com.example.services.impl;

import com.example.domain.entities.RecapByMonth;
import com.example.domain.exceptions.NoRecapFoundProblem;
import com.example.repository.RecapByMonthRepository;
import com.example.services.RecapByMonthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RecapByMonthServiceImpl implements RecapByMonthService {

    @Autowired
    RecapByMonthRepository recapByMonthRepository;

    @Override
    public RecapByMonth getRecapByMonth(Long id) {
        RecapByMonth recapByMonth = recapByMonthRepository.findById(id)
                .orElseThrow(() -> new NoRecapFoundProblem(id));
        return recapByMonth;
    }

    @Override
    public List<RecapByMonth> getAllRecapByMonth() {
        List<RecapByMonth> recapByMonths = new ArrayList<RecapByMonth>();
        recapByMonthRepository.findAll().forEach(recap -> recapByMonths.add(recap));
        return recapByMonths;
    }

    @Override
    public void delete(Long id) {
        RecapByMonth recap = getRecapByMonth(id);
        recapByMonthRepository.delete(recap);
    }

    @Override
    public void addOrUpdate(RecapByMonth recapByMonth) {
        recapByMonthRepository.save(recapByMonth);
    }
}
