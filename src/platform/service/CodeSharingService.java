package platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.controller.CodeSharingController;
import platform.model.Data;
import platform.repository.CodeSnippetsRepository;
import platform.utils.DataChecker;
import platform.utils.RestrictionsManager;
import platform.utils.UUIDGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CodeSharingService {

    @Autowired
    CodeSnippetsRepository codeSnippetsRepository;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeSharingService.class);

    public String addCode(Data codeSnippet) {
        if(codeSnippet == null) {
            return null;
        }

        String uniqueIdentifier = UUIDGenerator.generateUUID();
        LocalDateTime creationDate = LocalDateTime.now();
        creationDate.format(dtf);

        codeSnippet.setDate(creationDate);
        codeSnippet.setUUID(uniqueIdentifier);

        //Sets the expiry date field of the code snippet if the viewing time restriction is present
        long viewingTime = codeSnippet.getTime();
        if (viewingTime > 0) {
            LocalDateTime expiryDate = creationDate.plusSeconds(viewingTime);
            codeSnippet.setExpiryDate(expiryDate);
        }

        long numberOfViews = codeSnippet.getViews();
        if (numberOfViews > 0) {
            codeSnippet.setHasInitialViewsRestriction(true);
        }

        Data savedCodeSnippet = codeSnippetsRepository.save(codeSnippet);

        return savedCodeSnippet.getUUID();
    }

    @Transactional
    public Data getNthCodeSnippet(String UUID) {
        if (!codeSnippetsRepository.existsByUUID(UUID)) {
            return null;
        }

        Optional<Data> codeSnippet = codeSnippetsRepository.findByUUID(UUID);

        Data retrievedCodeSnippet = codeSnippet.get();

        long codeSnippetAvailability = retrievedCodeSnippet.getTime();
        long codeSnippetViews = retrievedCodeSnippet.getViews();

        //Checks if the code snippet has expired even though it might have never been accessed
        if (DataChecker.isCodeSnippetStale(retrievedCodeSnippet)) {
            codeSnippetsRepository.delete(retrievedCodeSnippet);
            return null;
        }


        if(retrievedCodeSnippet.isHasInitialViewsRestriction() && codeSnippetViews == 0) {
            codeSnippetsRepository.delete(retrievedCodeSnippet);
            return null;
        }


        if(codeSnippetAvailability > 0 && codeSnippetViews > 0) {
            if (DataChecker.isCodeSnippetAvailable(retrievedCodeSnippet)) {
                RestrictionsManager.updateCodeSnippetTime(retrievedCodeSnippet);
                RestrictionsManager.updateCodeSnippetViews(retrievedCodeSnippet);

                long updatedAvailability = retrievedCodeSnippet.getTime();
                long updatedViews = retrievedCodeSnippet.getViews();

                if (updatedAvailability == 0 || updatedViews < 0) {
                    retrievedCodeSnippet.setExpired(true);
                    retrievedCodeSnippet.setViews(0);
                }


                if (!retrievedCodeSnippet.isExpired()) {
                    codeSnippetsRepository.save(retrievedCodeSnippet);
                    return retrievedCodeSnippet;
                } else {
                    codeSnippetsRepository.delete(retrievedCodeSnippet);
                    return null;
                }

            } else {
                codeSnippetsRepository.delete(retrievedCodeSnippet);
                return null;
            }
        } else if (codeSnippetAvailability > 0){
            RestrictionsManager.updateCodeSnippetTime(retrievedCodeSnippet);

            if (retrievedCodeSnippet.getTime() == 0) {
                retrievedCodeSnippet.setExpired(true);
            }

            if (!retrievedCodeSnippet.isExpired()) {
                codeSnippetsRepository.save(retrievedCodeSnippet);
                return retrievedCodeSnippet;
            } else {
                codeSnippetsRepository.delete(retrievedCodeSnippet);
                return null;
            }

        } else if (retrievedCodeSnippet.getViews() > 0) {
            RestrictionsManager.updateCodeSnippetViews(retrievedCodeSnippet);
            long updatedViews = retrievedCodeSnippet.getViews();

            if (updatedViews < 0) {
                retrievedCodeSnippet.setExpired(true);
                retrievedCodeSnippet.setViews(0);
            }

            if (!retrievedCodeSnippet.isExpired()) {
                codeSnippetsRepository.save(retrievedCodeSnippet);
                return retrievedCodeSnippet;
            } else {
                codeSnippetsRepository.delete(retrievedCodeSnippet);
                return null;
            }
        }

        return retrievedCodeSnippet;
    }

    public List<Data> getFirstTenCodeSnippets() {

        List<Data> firstTenCodeSnippets = codeSnippetsRepository.findAllByOrderByDateDesc().stream()
                .filter(x -> x.getTime() <= 0 && x.getViews() <= 0)
                .limit(10)
                .collect(Collectors.toList());

        return firstTenCodeSnippets;
    }

    public Data getData(long id) {
        if (!codeSnippetsRepository.existsById(id)) {
            return null;
        }

        Optional<Data> codeSnippet = codeSnippetsRepository.findById(id);

        LOGGER.info("INSIDE GET DATA METHOD\nRETURNED CODE_SNIPPET: " + codeSnippet.get());


        return codeSnippet.get();
    }
}
