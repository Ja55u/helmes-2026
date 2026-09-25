package ee.helmes.sectors.submission;

import ee.helmes.sectors.sector.Sector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "submission")
public class Submission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "agree_to_terms", nullable = false)
  private boolean agreeToTerms;

  @ManyToMany
  @JoinTable(
      name = "submission_sector",
      joinColumns = @JoinColumn(name = "submission_id"),
      inverseJoinColumns = @JoinColumn(name = "sector_id"))
  private Set<Sector> sectors = new HashSet<>();

  public Submission() {}

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAgreeToTerms() {
    return agreeToTerms;
  }

  public void setAgreeToTerms(boolean agreeToTerms) {
    this.agreeToTerms = agreeToTerms;
  }

  public Set<Sector> getSectors() {
    return sectors;
  }

  public void replaceSectors(Collection<Sector> newSectors) {
    sectors.clear();
    sectors.addAll(newSectors);
  }
}
