package sep3.project.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sep3.project.jobservice.entities.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}
