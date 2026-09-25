package ee.helmes.sectors.submission;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submission")
class SubmissionController {
  static final String SESSION_KEY = "submissionId";

  private final SubmissionService service;

  SubmissionController(SubmissionService service) {
    this.service = service;
  }

  @GetMapping
  ResponseEntity<SubmissionResponse> current(HttpSession session) {
    Long id = (Long) session.getAttribute(SESSION_KEY);
    if (id == null) {
      return ResponseEntity.noContent().build();
    }

    Optional<SubmissionResponse> submission = service.find(id);
    if (submission.isEmpty()) {
      session.removeAttribute(SESSION_KEY);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(submission.get());
  }

  @PutMapping
  SubmissionResponse save(@Valid @RequestBody SubmissionRequest request, HttpSession session) {
    Long existingId = (Long) session.getAttribute(SESSION_KEY);
    SubmissionService.Saved saved = service.save(existingId, request);
    session.setAttribute(SESSION_KEY, saved.id());
    return saved.data();
  }
}
