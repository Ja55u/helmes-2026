package ee.helmes.sectors.common;

import ee.helmes.sectors.submission.UnknownSectorException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      String field = fieldError.getField();
      int bracketIndex = field.indexOf('[');
      if (bracketIndex >= 0) {
        field = field.substring(0, bracketIndex);
      }
      errors.putIfAbsent(field, fieldError.getDefaultMessage());
    }
    return ResponseEntity.badRequest().body(new ApiError("Validation failed", errors));
  }

  @ExceptionHandler(UnknownSectorException.class)
  ResponseEntity<ApiError> handleUnknownSector() {
    return ResponseEntity.badRequest()
        .body(new ApiError("Validation failed", Map.of("sectorIds", "Unknown sector selected")));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> handleMalformed() {
    return ResponseEntity.badRequest().body(new ApiError("Malformed request body", Map.of()));
  }
}
