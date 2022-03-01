package platform.utils;

import platform.model.Data;

import java.time.LocalDateTime;

public class DataChecker {

    public static boolean isCodeSnippetAvailable(Data codeSnippet) {
        if (codeSnippet == null) {
            return false;
        }

        LocalDateTime expiryDateTime = codeSnippet.getExpiryDate();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (currentDateTime.isBefore(expiryDateTime) || currentDateTime.isEqual(expiryDateTime)) {
            return true;
        }

        return false;
    }

    public static boolean isCodeSnippetStale(Data codeSnippet) {
        if (codeSnippet == null) {
            return false;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = codeSnippet.getExpiryDate();

        if (expiryDateTime != null && expiryDateTime.isBefore(currentDateTime)) {
            return true;
        }

        return false;
    }
}
