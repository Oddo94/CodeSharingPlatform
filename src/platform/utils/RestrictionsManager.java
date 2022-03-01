package platform.utils;

import platform.model.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RestrictionsManager {

    public static void updateCodeSnippetViews(Data codeSnippet) {
        if (codeSnippet == null || codeSnippet.getViews() == 0) {
            return;
        }

        long currentViews = codeSnippet.getViews();
        long viewsLeft = --currentViews;

        codeSnippet.setViews(viewsLeft);
    }

    public static void updateCodeSnippetTime(Data codeSnippet) {
        if(codeSnippet == null || codeSnippet.getTime() == 0) {
            return;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryDate = codeSnippet.getExpiryDate();
        long remainingTime = ChronoUnit.SECONDS.between(currentTime, expiryDate);


        codeSnippet.setTime(remainingTime);
    }
}
