package sep3.project.jobservice.grpc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.grpc.internal.testing.StreamRecorder;
import sep3.project.jobservice.repositories.JobRepository;

public class JobServiceImplTest {
    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobServiceImpl;

    private StreamRecorder<ListJobsResponse> responseObserver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        responseObserver = StreamRecorder.create();
    }

    @Test
    public void testJobServiceImplInitialization() throws Exception {
        assertNotNull(jobServiceImpl);
        assertNotNull(jobRepository);
    }
}
