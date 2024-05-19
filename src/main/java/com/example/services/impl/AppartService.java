package com.example.services.impl;

import com.example.domain.dto.AppartDto;
import com.example.domain.entities.Appart;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.entities.Loyer;
import com.example.domain.exceptions.BusinessException;
import com.example.domain.mapper.AppartMapper;
import com.example.repository.AppartRepository;
import com.example.repository.LoyerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static com.example.domain.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Transactional
public class AppartService {

    @Autowired
    private AppartRepository appartRepository;

    @Autowired
    private LoyerRepository loyerRepository;

    public Appart register(Appart appart) {
        Loyer newLoyerVide = new Loyer(appart);
        newLoyerVide.setDateLoyer(LocalDate.now());
        appartRepository.save(appart);
        loyerRepository.save(newLoyerVide);

        return appart;
    }

    public List<Appart> getAllAppartByLogement(Logement logement) {
        return appartRepository.findByLogement(logement);
    }

    public Appart getLogementApprtById(Logement logement, Long idAppart) throws BusinessException {

        return appartRepository.findByLogementAndId(logement, idAppart)
                .orElseThrow(() -> new BusinessException(String.format("No appart found with this id %d", idAppart),
                        NOT_FOUND));
    }

    public Appart getAppartById(Long id) throws BusinessException {
        return appartRepository.findById(id).
                orElseThrow(() -> new BusinessException(String.format("No appart found with this id %d", id),
                        NOT_FOUND));
    }

    public Appart updateLogementById(AppartDto appartDto, Long id) throws BusinessException {
        Appart appart = getAppartById(id);
        Appart appartUpdate = AppartMapper.getMapper().entitie(appartDto);
        AppartMapper.getMapper().update(appart, appartUpdate);
        appartRepository.save(appart);
        return appart;
    }

    public void deleteById(Long id) throws BusinessException {
        Appart appart = getAppartById(id);
        logger.info("Appartement id = " + appart.getId() + " is found");
        appartRepository.deleteById(id);
    }


    public List<AppartDto> rechercherSuggestionsParNom(String term) {
        return appartRepository.findSuggestionsByNomStartingWithIgnoreCase(term);
    }

    public Appart updateAppartAssigneLocataire(Long idAppart, Client locataire) throws BusinessException {
        Appart appart = getAppartById(idAppart);
        appart.setLocataire(locataire);
        appartRepository.save(appart);
        return appart;
    }

    private Logger logger = LoggerFactory.getLogger(AppartService.class);

    public Appart updateAppartSortirLocataire(Long idAppart) throws BusinessException {
        Appart appart = getAppartById(idAppart);
        appart.setLocataire(null);
        appartRepository.save(appart);
        return appart;
    }
}
