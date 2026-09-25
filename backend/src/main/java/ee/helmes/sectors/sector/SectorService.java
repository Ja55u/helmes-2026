package ee.helmes.sectors.sector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectorService {
  private final SectorRepository repository;

  public SectorService(SectorRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<SectorDto> findAll() {
    List<Sector> all = repository.findAllByOrderBySortOrderAsc();
    Map<Integer, Integer> depthById = new HashMap<>();
    List<SectorDto> result = new ArrayList<>(all.size());
    for (Sector sector : all) {
      Integer parentId = sector.getParentId();
      int depth = parentId == null ? 0 : depthById.get(parentId) + 1;
      depthById.put(sector.getId(), depth);
      result.add(new SectorDto(sector.getId(), sector.getName(), depth));
    }
    return result;
  }
}
