package ee.helmes.sectors.submission;

import ee.helmes.sectors.sector.Sector;
import java.util.List;

public record SubmissionResponse(String name, List<Integer> sectorIds, boolean agreeToTerms) {
  static SubmissionResponse from(Submission s) {
    List<Integer> ids = s.getSectors().stream().map(Sector::getId).sorted().toList();
    return new SubmissionResponse(s.getName(), ids, s.isAgreeToTerms());
  }
}
