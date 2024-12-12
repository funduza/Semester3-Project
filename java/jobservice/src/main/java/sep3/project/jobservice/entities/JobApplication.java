package sep3.project.jobservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Data
@Entity
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.InProgress;
    @CreatedDate
    private final Instant applicationDate = Instant.now();
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    @ManyToOne
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    public enum Status {
        InProgress, Approved, Declined
    }
}
