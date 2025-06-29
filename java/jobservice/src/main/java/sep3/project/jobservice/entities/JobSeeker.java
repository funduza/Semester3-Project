package sep3.project.jobservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobSeeker extends User {
    @NotBlank
    @Column(nullable = false)
    private String firstName;
    @NotBlank
    @Column(nullable = false)
    private String lastName;
    private String phoneNumber;
    @Column(columnDefinition = "TEXT")
    private String resume;
    @OneToMany(mappedBy = "jobSeeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications;

    @Builder(builderMethodName = "newBuilder", setterPrefix = "set")
    public JobSeeker(Long id, String email, String password, String firstName, String lastName, String phoneNumber, String resume) {
        super(id, email, password, Role.JobSeeker);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.resume = resume;
    }
}
