package ee.helmes.sectors.sector;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sectors")
class SectorController {
  private final SectorService service;

  SectorController(SectorService service) {
    this.service = service;
  }

  @GetMapping
  List<SectorDto> list() {
    return service.findAll();
  }
}
