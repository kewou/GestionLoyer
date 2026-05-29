package com.example.features.logement;

import com.example.exceptions.BusinessException;
import com.example.features.bail.Bail;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;

@Service
@Slf4j
@Transactional
public class LogementService implements LogementAppService {

    private final ClientAppService clientAppService;
    private final LogementRepository logementRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public LogementService(LogementRepository logementRepository, ClientAppService clientAppService,
                           TransactionRepository transactionRepository) {
        this.clientAppService = clientAppService;
        this.logementRepository = logementRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<LogementDto> getAllLogementByUser(String reference) throws BusinessException {
        Client bailleur = clientAppService.getClientFromDatabase(reference);
        return logementRepository.findByClient(bailleur).stream()
                .map(logement -> {
                    LogementDto dto = LogementMapper.getMapper().dto(logement);
                    // Total encaissé = somme de toutes les transactions du logement
                    Long totalEncaisse = transactionRepository.sumMontantByLogement(logement);
                    dto.setTotalEncaisse(totalEncaisse);
                    // Total endettement = somme des loyers attendus - somme encaissée, pour les baux actifs
                    long totalEndettement = 0L;
                    for (var appart : logement.getApparts()) {
                        for (Bail bail : appart.getBaux()) {
                            if (Boolean.TRUE.equals(bail.getActif())) {
                                LocalDate debut = bail.getDateEntree();
                                LocalDate fin = LocalDate.now();
                                long mois = ChronoUnit.MONTHS.between(debut.withDayOfMonth(1), fin.withDayOfMonth(1));
                                long loyersAttendus = mois * (appart.getPrixLoyer() != null ? appart.getPrixLoyer() : 0);
                                long loyerPaye = transactionRepository.sumMontantByBail(bail);
                                long dette = loyersAttendus - loyerPaye;
                                if (dette > 0) totalEndettement += dette;
                            }
                        }
                    }
                    dto.setTotalEndettement(totalEndettement);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public LogementDto register(String reference, LogementDto logementDto) throws BusinessException {
        Client client = clientAppService.getClientFromDatabase(reference);
        Logement logement = LogementMapper.getMapper().entitie(logementDto);
        if (logement.getReference() == "" || logement.getReference() == null) {
            logement.setReference(GeneralUtils.generateReference());
        }
        logement.setClient(client);
        logementRepository.save(logement);
        log.info(LOGEMENT_LOG + logement.getReference() + " is created");
        return LogementMapper.getMapper().dto(logement);
    }


    public LogementDto getUserLogementByRef(String refUser, String refLgt) throws BusinessException {
        Client bailleur = clientAppService.getClientFromDatabase(refUser);
        Logement lgt = logementRepository.findByClientAndReference(bailleur, refLgt)
                .orElseThrow(() -> new BusinessException(String.format("No logement found with this ref %s", refLgt), NOT_FOUND));
        return LogementMapper.getMapper().dto(lgt);
    }

    public LogementDto getLogementByReference(String refLgt) throws BusinessException {
        Logement lgt = this.getLogementFromDatabase(refLgt);
        return LogementMapper.getMapper().dto(lgt);
    }

    public LogementDto updateLogementByReference(LogementDto logementDto, String refLgt) throws BusinessException {
        Logement logement = this.getLogementFromDatabase(refLgt);
        Logement logementUpdate = LogementMapper.getMapper().entitie(logementDto);
        LogementMapper.getMapper().update(logement, logementUpdate);
        logementRepository.save(logement);
        log.info(LOGEMENT_LOG + logement.getReference() + " is saved");
        return LogementMapper.getMapper().dto(logement);
    }

    public void deleteByReference(String refLgt) throws BusinessException {
        Logement logement = getLogementFromDatabase(refLgt);
        logementRepository.deleteByReference(refLgt);
        log.info(LOGEMENT_LOG + logement.getReference() + " is deleted");
    }

    public Logement getLogementFromDatabase(String refLgt) throws BusinessException {
        Logement lgt = logementRepository.findByReference(refLgt).
                orElseThrow(() -> new BusinessException(String.format("No logement found with this ref %s", refLgt), NOT_FOUND));
        log.info(LOGEMENT_LOG + lgt.getReference() + " is found");
        return lgt;
    }


    private static final String LOGEMENT_LOG = "Logement ref = ";

}


