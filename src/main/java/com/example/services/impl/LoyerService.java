package com.example.services.impl;

import com.example.repository.LoyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class LoyerService {

    @Autowired
    private LoyerRepository loyerRepository;


}
