package sep3.project.jobservice.grpc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.grpc.internal.testing.StreamRecorder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.repositories.JobProviderRepository;
import sep3.project.jobservice.repositories.JobRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {
    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobProviderRepository jobProviderRepository;

    @InjectMocks
    private JobServiceImpl jobServiceImpl;

    @Test
    public void testJobServiceImplInitialization() {
        assertNotNull(jobServiceImpl);
        assertNotNull(jobRepository);
    }

    @Test
    public void testCreateJob() throws Exception {
        // Arrange
        Job testJob = getJob();
        JobProto testJobProto = JobProto.newBuilder()
                .setId(testJob.getId())
                .setTitle(testJob.getTitle())
                .setDescription(testJob.getDescription())
                .setPostingDate(Timestamp.newBuilder()
                        .setSeconds(testJob.getPostingDate().getEpochSecond())
                        .setNanos(testJob.getPostingDate().getNano())
                        .build())
                .setDeadline(Timestamp.newBuilder()
                        .setSeconds(testJob.getDeadline().getEpochSecond())
                        .setNanos(testJob.getDeadline().getNano())
                        .build())
                .setLocation(testJob.getLocation())
                .setSalary(testJob.getSalary())
                .setType(testJob.getType().toString())
                .setStatus(testJob.getStatus().toString())
                .setJobProvider(UserProto.newBuilder()
                        .setId(testJob.getJobProvider().getId())
                        .setEmail(testJob.getJobProvider().getEmail())
                        .setRole(testJob.getJobProvider().getRole().toString())
                        .setJobProvider(JobProviderProto.newBuilder()
                                .setName(testJob.getJobProvider().getName())
                                .setDescription(testJob.getJobProvider().getDescription())
                                .setPhoneNumber(testJob.getJobProvider().getPhoneNumber())
                                .build())
                        .build())
                .build();
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);
        when(jobProviderRepository.findById(testJob.getJobProvider().getId())).thenReturn(Optional.of(testJob.getJobProvider()));

        StreamRecorder<JobProto> response = StreamRecorder.create();

        // Act
        jobServiceImpl.createJob(CreateJobRequest.newBuilder().setJob(testJobProto).build(), response);

        // Assert
        assertNull(response.getError());
        assertEquals(1, response.getValues().size());
        assertEquals(testJob.getId(), response.firstValue().get().getId());

        verify(jobRepository, times(1)).save(any(Job.class));
        verify(jobProviderRepository, times(1)).findById(testJob.getJobProvider().getId());
    }

    @ParameterizedTest(name = "{index} => pageSize={0}, pageToken={1}, expectedJobsCount={2}")
    @CsvSource({
            "0, '', 12", // Zero
            "6, '0', 6", // One
            "70, '1', 64" // Boundary
    })
    public void testListJobs(int pageSize, String pageToken, int expectedJobsCount) throws Exception {
        // Arrange
        List<Job> jobs = IntStream.range(1, 129).boxed().map(this::getJob).toList();
        when(jobRepository.findAll(any(Specification.class), any(Pageable.class))).thenAnswer(invocation -> {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), jobs.size());
            return new PageImpl<>(jobs.subList(start, end), pageable, jobs.size());
        });

        StreamRecorder<ListJobsResponse> recorder = StreamRecorder.create();

        // Act
        jobServiceImpl.listJobs(ListJobsRequest.newBuilder().setPageSize(pageSize).setPageToken(pageToken).build(), recorder);

        // Assert
        assertNull(recorder.getError());
        assertEquals(1, recorder.getValues().size());
        assertEquals(expectedJobsCount, recorder.firstValue().get().getJobsCount());

        verify(jobRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testGetJob() throws Exception {
        // Arrange
        Job testJob = getJob();
        when(jobRepository.findById(testJob.getId())).thenReturn(Optional.of(testJob));

        StreamRecorder<JobProto> response = StreamRecorder.create();

        // Act
        jobServiceImpl.getJob(GetJobRequest.newBuilder().setId(1).build(), response);

        // Assert
        assertNull(response.getError());
        assertEquals(1, response.getValues().size());
        assertEquals(1L, response.firstValue().get().getId());

        assertThrows(NoSuchElementException.class, () -> jobServiceImpl.getJob(GetJobRequest.newBuilder().setId(2).build(), response));
        verify(jobRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testUpdateJob() throws Exception {
        // Arrange
        Job testJob = getJob();
        when(jobRepository.findById(testJob.getId())).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        StreamRecorder<JobProto> response = StreamRecorder.create();

        // Act
        jobServiceImpl.updateJob(UpdateJobRequest.newBuilder()
                .setId(testJob.getId())
                .setStatus(Job.Status.Closed.toString())
                .build(),
                response
        );

        // Assert
        assertNull(response.getError());
        assertEquals(1, response.getValues().size());
        assertEquals(testJob.getId(), response.firstValue().get().getId());
        assertEquals(Job.Status.Closed.toString(), response.firstValue().get().getStatus());

        assertThrows(NoSuchElementException.class, () -> jobServiceImpl.updateJob(UpdateJobRequest.newBuilder().setId(2).build(), response));
        verify(jobRepository, times(1)).findById(testJob.getId());
        verify(jobRepository, times(1)).save(any(Job.class));
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
