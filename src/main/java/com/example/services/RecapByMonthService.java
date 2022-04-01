package com.example.services;

import com.example.entities.RecapByMonth;
import com.example.entities.User;

import java.util.List;

public interface RecapByMonthService {

    RecapByMonth getRecapByMonth(Long id);

    List<RecapByMonth> getAllRecapByMonth();

    void delete(Long id);

    void addOrUpdate(RecapByMonth recapByMonth);
}
