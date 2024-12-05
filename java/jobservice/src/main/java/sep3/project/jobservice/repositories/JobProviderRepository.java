package sep3.project.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sep3.project.jobservice.entities.JobProvider;

public interface JobProviderRepository extends JpaRepository<JobProvider, Long> {
}
