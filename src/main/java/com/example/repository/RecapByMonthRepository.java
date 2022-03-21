package com.example.repository;

import com.example.entities.RecapByMonth;
import com.example.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecapByMonthRepository extends CrudRepository<RecapByMonth, Long> {
}
