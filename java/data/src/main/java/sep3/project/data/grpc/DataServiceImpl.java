package sep3.project.data.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sep3.project.data.entities.Job;
import sep3.project.data.repositories.JobRepository;

import java.util.List;

@Slf4j
@GrpcService
public class DataServiceImpl extends sep3.project.data.grpc.DataGrpc.DataImplBase {
    private final JobRepository jobRepository;

    public DataServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void getAllJobs(sep3.project.data.grpc.GetAllJobsRequest request, StreamObserver<sep3.project.data.grpc.GetAllJobsResponse> responseObserver) {
        log.info("GetAllJobsRequest: {}", request);

        List<Job> jobs = jobRepository.findAll();

        List<sep3.project.data.grpc.GetAllJobsResponse.Job> jobMessages = jobs.stream()
                .map(job -> sep3.project.data.grpc.GetAllJobsResponse.Job.newBuilder()
                        .setId(job.getId()).
                        setTitle(job.getTitle()).
                        setDescription(job.getDescription()).
                        setSalary(job.getSalary()).
                        setType(job.getType().toString()).
                        setDeadline(job.getDeadline().toString()).
                        setLocation(job.getLocation())
                        .build())
                .toList();

        sep3.project.data.grpc.GetAllJobsResponse response = sep3.project.data.grpc.GetAllJobsResponse
                .newBuilder()
                .addAllJobs(jobMessages)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
