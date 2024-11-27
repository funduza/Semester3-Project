package sep3.project.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sep3.project.data.entities.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByTitleContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String title, String description, Pageable pageable);
}
