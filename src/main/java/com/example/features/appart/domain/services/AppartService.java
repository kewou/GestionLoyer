package com.example.features.appart.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.application.mapper.AppartMapper;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.logement.application.appService.LogementAppService;
import com.example.features.logement.domain.entities.Logement;
import com.example.features.loyer.domain.entities.Loyer;
import com.example.features.loyer.infra.LoyerRepository;
import com.example.features.user.domain.entities.Client;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class AppartService implements AppartAppService {

    private LogementAppService logementAppService;
    private AppartRepository appartRepository;
    private LoyerRepository loyerRepository;

    @Autowired
    public AppartService(LogementAppService logementAppService, AppartRepository appartRepository, LoyerRepository loyerRepository) {
        this.logementAppService = logementAppService;
        this.appartRepository = appartRepository;
        this.loyerRepository = loyerRepository;
    }

    public List<AppartDto> getAllAppartByLogement(String refLgt) throws BusinessException {
        Logement lgt = logementAppService.getLogementFromDatabase(refLgt);
        return appartRepository.findByLogement(lgt).stream()
                .map(AppartMapper.getMapper()::dto)
                .collect(Collectors.toList());
    }

    public Appart register(String refLgt, AppartDto appartDto) throws BusinessException {
        Logement logement = logementAppService.getLogementFromDatabase(refLgt);
        Appart appart = AppartMapper.getMapper().entitie(appartDto);
        appart.setLogement(logement);
        appart.setBailleur(logement.getClient());
        Loyer newLoyerVide = new Loyer(appart);
        newLoyerVide.setDateLoyer(LocalDate.now());
        if (newLoyerVide.getReference() == null) {
            newLoyerVide.setReference(GeneralUtils.generateReference());
        }
        if (appart.getReference() == null) {
            appart.setReference(GeneralUtils.generateReference());
        }
        appartRepository.save(appart);
        loyerRepository.save(newLoyerVide);
        log.info("Appart ref = " + logement.getReference() + " is created");
        return appart;
    }


    public AppartDto getLogementApprtByRef(String refLgt, String refAppart) throws BusinessException {
        Logement logement = logementAppService.getLogementFromDatabase(refLgt);
        Appart appart = appartRepository.findByLogementAndReference(logement, refAppart)
                .orElseThrow(() -> new BusinessException(String.format("No appart found with this reference %s", refAppart),
                        NOT_FOUND));
        return AppartMapper.getMapper().dto(appart);
    }

    public AppartDto getAppartByRef(String refAppart) throws BusinessException {
        Appart appart = getAppartFromDatabase(refAppart);
        return AppartMapper.getMapper().dto(appart);
    }

    public AppartDto updateLogementByRef(AppartDto appartDto, String refAppart) throws BusinessException {
        Appart appart = this.getAppartFromDatabase(refAppart);
        Appart appartUpdate = AppartMapper.getMapper().entitie(appartDto);
        AppartMapper.getMapper().update(appart, appartUpdate);
        appartRepository.save(appart);
        log.info("Appart ref = " + appart.getReference() + " is updated");
        return AppartMapper.getMapper().dto(appart);
    }

    public void deleteByRef(String refAppart) throws BusinessException {
        Appart appart = this.getAppartFromDatabase(refAppart);
        appartRepository.deleteByReference(refAppart);
        log.info("Appartement reference = " + appart.getReference() + " is deleted");
    }


    public List<AppartDto> rechercherSuggestionsParNom(String term) {
        return appartRepository.findSuggestionsByNomStartingWithIgnoreCase(term);
    }

    public AppartDto updateAppartAssigneLocataire(String refAppart, String refLgt) throws BusinessException {
        Appart appart = this.getAppartFromDatabase(refAppart);
        Client locataire = logementAppService.getLogementFromDatabase(refLgt).getClient();
        appart.setLocataire(locataire);
        appartRepository.save(appart);
        log.info("Appart ref = " + appart.getReference() + " is updated");
        return AppartMapper.getMapper().dto(appart);
    }


    public AppartDto updateAppartSortirLocataire(String refAppart) throws BusinessException {
        Appart appart = this.getAppartFromDatabase(refAppart);
        appart.setLocataire(null);
        appartRepository.save(appart);
        log.info("Appart ref = " + appart.getReference() + " is updated");
        return AppartMapper.getMapper().dto(appart);
    }

    public Appart getAppartFromDatabase(String refAppart) throws BusinessException {
        Appart appart = appartRepository.findByReference(refAppart).
                orElseThrow(() -> new BusinessException(String.format("No appart found with this reference %s", refAppart),
                        NOT_FOUND));
        log.info("Appart ref = " + appart.getReference() + " is found");
        return appart;
    }
}
