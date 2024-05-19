package com.example.features.appart.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.application.mapper.AppartMapper;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
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

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class AppartService implements AppartAppService {


    private AppartRepository appartRepository;
    private LoyerRepository loyerRepository;

    @Autowired
    public AppartService(AppartRepository appartRepository, LoyerRepository loyerRepository) {
        this.appartRepository = appartRepository;
        this.loyerRepository = loyerRepository;
    }

    public Appart register(Appart appart) {
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

        return appart;
    }

    public List<Appart> getAllAppartByLogement(Logement logement) {
        return appartRepository.findByLogement(logement);
    }

    public Appart getLogementApprtByRef(Logement logement, String refAppart) throws BusinessException {
        return appartRepository.findByLogementAndReference(logement, refAppart)
                .orElseThrow(() -> new BusinessException(String.format("No appart found with this reference %s", refAppart),
                        NOT_FOUND));
    }

    public Appart getAppartByRef(String refAppart) throws BusinessException {
        return appartRepository.findByReference(refAppart).
                orElseThrow(() -> new BusinessException(String.format("No appart found with this reference %s", refAppart),
                        NOT_FOUND));
    }

    public Appart updateLogementByRef(AppartDto appartDto, String refAppart) throws BusinessException {
        Appart appart = getAppartByRef(refAppart);
        Appart appartUpdate = AppartMapper.getMapper().entitie(appartDto);
        AppartMapper.getMapper().update(appart, appartUpdate);
        appartRepository.save(appart);
        return appart;
    }

    public void deleteByRef(String refAppart) throws BusinessException {
        Appart appart = getAppartByRef(refAppart);
        log.info("Appartement reference = " + appart.getReference() + " is found");
        appartRepository.deleteByReference(refAppart);
    }


    public List<AppartDto> rechercherSuggestionsParNom(String term) {
        return appartRepository.findSuggestionsByNomStartingWithIgnoreCase(term);
    }

    public Appart updateAppartAssigneLocataire(String refAppart, Client locataire) throws BusinessException {
        Appart appart = getAppartByRef(refAppart);
        appart.setLocataire(locataire);
        appartRepository.save(appart);
        return appart;
    }


    public Appart updateAppartSortirLocataire(String refAppart) throws BusinessException {
        Appart appart = getAppartByRef(refAppart);
        appart.setLocataire(null);
        appartRepository.save(appart);
        return appart;
    }
}
