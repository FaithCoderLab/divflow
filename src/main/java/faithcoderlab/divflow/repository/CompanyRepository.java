package faithcoderlab.divflow.repository;

import faithcoderlab.divflow.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    boolean existsByTicker(String ticker);

    List<Company> findByNameStartingWithIgnoreCase(String prefix);
}
