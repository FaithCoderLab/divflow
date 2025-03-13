package faithcoderlab.divflow.repository;

import faithcoderlab.divflow.model.Company;
import faithcoderlab.divflow.model.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {
    List<Dividend> findAllByCompany(Company company);
}
