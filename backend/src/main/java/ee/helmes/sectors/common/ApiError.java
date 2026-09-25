package ee.helmes.sectors.common;

import java.util.Map;

public record ApiError(String message, Map<String, String> errors) {}
