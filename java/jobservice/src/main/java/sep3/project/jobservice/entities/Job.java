package sep3.project.jobservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Represents a job in the system. Jobs can be created by job providers and applied to
 * by job seekers. Each job has a unique id and a job provider.
 */
@Data
@Entity
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The title of the job. Must be provided.
     */
    @NotBlank
    private String title;
    /**
     * The description of the job. Can include any text that helps
     * the job seeker to decide if they should apply to this job.
     */
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;
    /**
     * The date when this job was posted. Defaults to the current date
     * when the entity was created.
     */
    private final Instant postingDate = Instant.now();
    /**
     * The date until which job seekers can apply to this job. Must be a date in the future.
     */
    @Future
    private Instant deadline;
    /**
     * The location of the job. Must be provided.
     */
    @NotBlank
    private String location;
    /**
     * The monthly salary of this job. Must be a positive integer.
     */
    @Positive
    private Integer salary;
    /**
     * The type of employment of this job. Must be one of PartTime, FullTime, Internship.
     */
    @Enumerated(EnumType.STRING)
    private Type type;
    /**
     * The status of this job. By default, newly created jobs have a status of `Active`.
     * This can later be changed to `Closed` if it is no longer accepting applications.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.Active;
    /**
     * The job provider that created this job.
     */
    @ManyToOne
    @JoinColumn(name = "job_provider_id", nullable = false)
    private JobProvider jobProvider;
    /**
     * The job applications related to this job.
     */
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications;

    public enum Type {
        PartTime, FullTime, Internship
    }

    public enum Status {
        Active, Closed
    }
}
