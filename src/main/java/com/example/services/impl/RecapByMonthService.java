package com.example.services.impl;

import com.example.domain.entities.RecapByMonth;
import com.example.domain.exceptions.NoRecapFoundException;
import com.example.repository.RecapByMonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RecapByMonthService {

    @Autowired
    RecapByMonthRepository recapByMonthRepository;


    public RecapByMonth getRecapByMonth(Long id) throws NoRecapFoundException {
        RecapByMonth recapByMonth = recapByMonthRepository.findById(id)
                .orElseThrow(() -> new NoRecapFoundException(id));
        return recapByMonth;
    }


    public List<RecapByMonth> getAllRecapByMonth() {
        List<RecapByMonth> recapByMonths = new ArrayList<RecapByMonth>();
        recapByMonthRepository.findAll().forEach(recap -> recapByMonths.add(recap));
        return recapByMonths;
    }


    public void delete(Long id) throws NoRecapFoundException {
        RecapByMonth recap = getRecapByMonth(id);
        recapByMonthRepository.delete(recap);
    }


    public void addOrUpdate(RecapByMonth recapByMonth) {
        recapByMonthRepository.save(recapByMonth);
    }
}
