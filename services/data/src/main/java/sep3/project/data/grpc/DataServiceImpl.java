package sep3.project.data.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sep3.project.data.entities.Job;
import sep3.project.data.repositories.JobRepository;

import java.util.List;

@Slf4j
@GrpcService
public class DataServiceImpl extends DataGrpc.DataImplBase {
    private final JobRepository jobRepository;

    public DataServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void getAllJobs(GetAllJobsRequest request, StreamObserver<GetAllJobsResponse> responseObserver) {
        log.info("GetAllJobsRequest: {}", request);

        List<Job> jobs = jobRepository.findAll();
        Iterable<GetAllJobsResponse.Job> jobMessages = jobs.stream()
                .map(job -> GetAllJobsResponse.Job.newBuilder()
                        .setId(job.getId()).
                        setTitle(job.getTitle()).build()).toList();

        GetAllJobsResponse response = GetAllJobsResponse
                .newBuilder()
                .addAllJobs(jobMessages)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
