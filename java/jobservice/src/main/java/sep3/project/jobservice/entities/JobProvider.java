package sep3.project.jobservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobProvider extends User {
    @NotBlank
    @Column(nullable = false)
    private String name;
    private String description;
    private String phoneNumber;
    @OneToMany(mappedBy = "jobProvider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs;

    @Builder(builderMethodName = "newBuilder", setterPrefix = "set")
    public JobProvider(Long id, String email, String password, String name, String description, String phoneNumber) {
        super(id, email, password, Role.JobProvider);
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
    }
}
