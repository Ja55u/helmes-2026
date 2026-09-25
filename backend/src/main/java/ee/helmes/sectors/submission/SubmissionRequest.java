package ee.helmes.sectors.submission;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record SubmissionRequest(
    @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,
    @NotEmpty(message = "Select at least one sector") Set<@NotNull Integer> sectorIds,
    @NotNull(message = "You must agree to the terms")
        @AssertTrue(message = "You must agree to the terms")
        Boolean agreeToTerms) {
  public SubmissionRequest {
    name = name == null ? null : name.strip();
  }
}
