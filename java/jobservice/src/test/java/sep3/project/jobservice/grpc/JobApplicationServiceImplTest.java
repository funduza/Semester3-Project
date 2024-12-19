package sep3.project.jobservice.grpc;

import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobApplication;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.entities.JobSeeker;
import sep3.project.jobservice.repositories.JobApplicationRepository;
import sep3.project.jobservice.repositories.JobRepository;
import sep3.project.jobservice.repositories.JobSeekerRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobApplicationServiceImplTest {
    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobSeekerRepository jobSeekerRepository;
    @InjectMocks
    private JobApplicationServiceImpl jobApplicationServiceImpl;

    @Test
    public void testJobApplicationServiceImplInitialization() {
        assertNotNull(jobApplicationServiceImpl);
        assertNotNull(jobApplicationRepository);
    }

    @Test
    void testCreateJobApplication() throws Exception
    {
        // Arrange
        JobApplication testJobApplication = JobApplication.newBuilder()
                .setId(1L)
                .setJob(getJob())
                .setJobSeeker(getJobSeeker())
                .build();

        JobApplicationProto testJobApplicationProto = JobApplicationProto.newBuilder()
                .setId(testJobApplication.getId())
                .setApplicationDate(testJobApplication.getApplicationDate().toString())
                .setJobSeekerId(testJobApplication.getJobSeeker().getId())
                .setJobId(testJobApplication.getJob().getId())
                .setStatus(testJobApplication.getStatus().toString())
                .build();

        when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(testJobApplication);
        when(jobRepository.findById(testJobApplication.getJob().getId())).thenReturn(Optional.of(testJobApplication.getJob()));
        when(jobSeekerRepository.findById(testJobApplication.getJobSeeker().getId())).thenReturn(Optional.of(testJobApplication.getJobSeeker()));

        StreamRecorder<JobApplicationProto> response = StreamRecorder.create();

        // Act
        jobApplicationServiceImpl.createJobApplication(CreateJobApplicationRequest.newBuilder().setJobApplication(testJobApplicationProto).build(), response);

        // Assert
        assertNotNull(response);
        assertEquals(1,response.getValues().size());
        assertEquals(testJobApplication.getId(), response.firstValue().get().getId());

        verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
        verify(jobRepository, times(1)).findById(testJobApplication.getJob().getId());
        verify(jobSeekerRepository, times(1)).findById(testJobApplication.getJobSeeker().getId());
    }

    /**
     * Helper method to get a test job Seeker.
     * @param id The entity identifier
     * @return The test job Seeker
     */
    private JobSeeker getJobSeeker(long id) {
        return JobSeeker.newBuilder()
                .setId(id)
                .setEmail("test@test.test")
                .setPassword("test")
                .setFirstName("firstTest")
                .setLastName("lastTest")
                .setPhoneNumber("1000")
                .setResume("test is test")
                .build();
    }

    /**
     * Helper method to get a test jobSeeker with a default id of 1.
     * @return The test job Seeker
     */
    private JobSeeker getJobSeeker() {
        return getJobSeeker(1L);
    }

    /**
     * Helper method to get a test job provider.
     * @param id The entity identifier
     * @return The test job provider
     */
    private JobProvider getJobProvider(long id) {
        return JobProvider.newBuilder()
                .setId(id)
                .setEmail("test@test.test")
                .setPassword("test")
                .setName("test")
                .setDescription("test")
                .setPhoneNumber("1000")
                .build();
    }

    /**
     * Helper method to get a test job provider with a default id of 1.
     * @return The test job provider
     */
    private JobProvider getJobProvider() {
        return getJobProvider(1L);
    }

    /**
     * Helper method to get a test job.
     * @param id The entity identifier
     * @return The test job
     */
    private Job getJob(long id) {
        return Job.newBuilder()
                .setId(id)
                .setTitle("test")
                .setDescription("test")
                .setDeadline(Instant.now().plus(30, ChronoUnit.DAYS))
                .setLocation("test")
                .setSalary(1000)
                .setType(Job.Type.FullTime)
                .setJobProvider(getJobProvider(id))
                .build();
    }

    /**
     * Helper method to get a test job with a default id of 1.
     * @return The test job
     */
    private Job getJob() {
        return getJob(1L);
    }
}