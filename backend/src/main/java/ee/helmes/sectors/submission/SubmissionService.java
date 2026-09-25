package ee.helmes.sectors.submission;

import ee.helmes.sectors.sector.Sector;
import ee.helmes.sectors.sector.SectorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionService {
  public record Saved(Long id, SubmissionResponse data) {}

  private final SubmissionRepository submissionRepository;
  private final SectorRepository sectorRepository;

  public SubmissionService(
      SubmissionRepository submissionRepository, SectorRepository sectorRepository) {
    this.submissionRepository = submissionRepository;
    this.sectorRepository = sectorRepository;
  }

  @Transactional(readOnly = true)
  public Optional<SubmissionResponse> find(Long id) {
    return submissionRepository.findById(id).map(SubmissionResponse::from);
  }

  @Transactional
  public Saved save(Long existingId, SubmissionRequest request) {
    List<Sector> sectors = sectorRepository.findAllById(request.sectorIds());
    if (sectors.size() != request.sectorIds().size()) {
      throw new UnknownSectorException();
    }
    Submission submission;
    if (existingId == null) {
      submission = new Submission();
    } else {
      submission = submissionRepository.findById(existingId).orElseGet(Submission::new);
    }
    submission.setName(request.name());
    submission.setAgreeToTerms(request.agreeToTerms());
    submission.replaceSectors(sectors);
    Submission saved = submissionRepository.saveAndFlush(submission);
    return new Saved(saved.getId(), SubmissionResponse.from(saved));
  }
}
