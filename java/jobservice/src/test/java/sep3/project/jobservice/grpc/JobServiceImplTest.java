package sep3.project.jobservice.grpc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import sep3.project.jobservice.repositories.JobRepository;

import java.util.*;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobServiceImpl;

    @Test
    public void testJobServiceImplInitialization() {
        assertNotNull(jobServiceImpl);
        assertNotNull(jobRepository);
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

    private JobProvider getJobProvider() {
        return getJobProvider(1L);
    }

    private Job getJob(long id) {
        return Job.newBuilder()
                .setId(id)
                .setTitle("test")
                .setDescription("test")
                .setPostingDate(new Date())
                .setDeadline(new Date())
                .setLocation("test")
                .setType(Job.Type.FullTime)
                .setSalary(1000.0)
                .setStatus(Job.Status.Active)
                .setJobProvider(getJobProvider(id))
                .build();
    }

    private Job getJob() {
        return getJob(1L);
    }
}
