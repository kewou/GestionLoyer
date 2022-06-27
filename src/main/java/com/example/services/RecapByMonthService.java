package com.example.services;

import com.example.domain.entities.RecapByMonth;

import java.util.List;

public interface RecapByMonthService {

    RecapByMonth getRecapByMonth(Long id);

    List<RecapByMonth> getAllRecapByMonth();

    void delete(Long id);

    void addOrUpdate(RecapByMonth recapByMonth);
}
