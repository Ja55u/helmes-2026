package ee.helmes.sectors.sector;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Integer> {
  List<Sector> findAllByOrderBySortOrderAsc();
}
