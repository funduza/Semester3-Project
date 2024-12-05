package sep3.project.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sep3.project.jobservice.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
