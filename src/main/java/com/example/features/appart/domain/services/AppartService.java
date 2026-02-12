package com.example.features.appart.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.application.mapper.AppartMapper;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.bail.BailMapper;
import com.example.features.bail.BailRepository;
import com.example.features.logement.Logement;
import com.example.features.logement.LogementAppService;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class AppartService implements AppartAppService {

    private final LogementAppService logementAppService;
    private final AppartRepository appartRepository;

    private final BailRepository bailRepository;
    private final BailMapper bailMapper;
    private final AppartMapper appartMapper;


    @Autowired
    public AppartService(LogementAppService logementAppService, AppartRepository appartRepository,
                         BailRepository bailRepository, BailMapper bailMapper, AppartMapper appartMapper) {
        this.logementAppService = logementAppService;
        this.appartRepository = appartRepository;
        this.bailRepository = bailRepository;
        this.bailMapper = bailMapper;
        this.appartMapper = appartMapper;
    }

    public List<AppartDto> getAllAppartByLogement(String refLgt) throws BusinessException {
        Logement lgt = logementAppService.getLogementFromDatabase(refLgt);
        return appartRepository.findByLogement(lgt).stream()
                .map(appartMapper::dto)
                .collect(Collectors.toList());
    }

    public AppartDto register(String refLgt, AppartDto appartDto) throws BusinessException {
        Logement logement = logementAppService.getLogementFromDatabase(refLgt);
        Appart appart = appartMapper.entitie(appartDto);
        appart.setLogement(logement);
        appart.setBailleur(logement.getClient());
        if (appart.getReference() == "" || appart.getReference() == null) {
            appart.setReference(GeneralUtils.generateReference());
        }
        appartRepository.save(appart);
        log.info(APPART_LOG + logement.getReference() + " is created");
        return appartMapper.dto(appart);
    }


    public AppartDto getLogementApprtByRef(String refLgt, String refAppart) throws BusinessException {
        Logement logement = logementAppService.getLogementFromDatabase(refLgt);
        Appart appart = appartRepository.findByLogementAndReference(logement, refAppart)
                .orElseThrow(() -> new BusinessException(String.format("No appart found with this reference %s", refAppart),
                        NOT_FOUND));
        AppartDto appartDto = appartMapper.dto(appart);
        return appartDto;
    }

    public AppartDto getAppartByRef(String refAppart) throws BusinessException {
        Appart appart = getAppartFromDatabase(refAppart);
        return appartMapper.dto(appart);
    }

    public AppartDto updateLogementByRef(AppartDto appartDto, String refAppart) throws BusinessException {
        Appart appart = this.getAppartFromDatabase(refAppart);
        Appart appartUpdate = appartMapper.entitie(appartDto);
        appartMapper.update(appart, appartUpdate);
        appartRepository.save(appart);
        log.info(APPART_LOG + appart.getReference() + " is updated");
        return appartMapper.dto(appart);
    }

    public void deleteByRef(String refAppart) throws BusinessException {
        Appart appart = this.getAppartFromDatabase(refAppart);
        appartRepository.deleteByReference(refAppart);
        log.info(APPART_LOG + appart.getReference() + " is deleted");
    }


    public Appart getAppartFromDatabase(String refAppart) throws BusinessException {
        Appart appart = appartRepository.findByReference(refAppart).
                orElseThrow(() -> new BusinessException(String.format("No appart found with this reference %s", refAppart),
                        NOT_FOUND));
        log.info(APPART_LOG + appart.getReference() + " is found");
        return appart;
    }

    private static final String APPART_LOG = "Appart ref = ";
}
