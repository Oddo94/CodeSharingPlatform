package platform.utils;

import java.util.UUID;

public class UUIDGenerator {

    public static String generateUUID() {
        UUID uniqueIdentifier = UUID.randomUUID();

        return uniqueIdentifier.toString();

    }
}
