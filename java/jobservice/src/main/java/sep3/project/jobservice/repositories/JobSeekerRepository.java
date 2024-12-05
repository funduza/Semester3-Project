package sep3.project.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sep3.project.jobservice.entities.JobSeeker;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {
}
