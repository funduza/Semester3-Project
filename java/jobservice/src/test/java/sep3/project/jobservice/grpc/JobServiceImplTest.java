package sep3.project.jobservice.grpc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import io.grpc.internal.testing.StreamRecorder;
import org.mockito.junit.jupiter.MockitoExtension;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.repositories.JobRepository;

import java.util.*;

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
