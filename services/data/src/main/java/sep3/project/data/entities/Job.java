package sep3.project.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String description;
    private Date postingDate;
    private Date deadline;
    private String location;
    private Type type;
    private Double salary;
    private Status status;

    public enum Type {
        PartTime, FullTime
    }

    public enum Status {
        Active, Closed
    }
}
