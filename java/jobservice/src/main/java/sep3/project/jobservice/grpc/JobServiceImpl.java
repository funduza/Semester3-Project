package sep3.project.jobservice.grpc;

import io.github.perplexhub.rsql.RSQLJPASupport;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.repositories.JobRepository;

import java.util.List;

@Slf4j
@GrpcService
public class JobServiceImpl extends JobServiceGrpc.JobServiceImplBase {
    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void listJobs(ListJobsRequest request, StreamObserver<ListJobsResponse> responseObserver) {
        log.info("GetAllJobsRequest: {}", request);

        int pageSize = request.getPageSize() == 0 ? 12 : request.getPageSize() > 64 ? 64 : request.getPageSize();
        int pageToken;

        try {
            pageToken = Integer.parseInt(request.getPageToken());
        } catch (Exception e) {
            pageToken = 0;
        }

        Specification<Job> specification = RSQLJPASupport.toSpecification(request.getFilter());
        Page<Job> jobs = jobRepository.findAll(specification, PageRequest.of(pageToken, pageSize));

        List<JobProto> jobMessages = jobs.stream()
                .map(job -> JobProto.newBuilder()
                        .setId(job.getId())
                        .setTitle(job.getTitle())
                        .setDescription(job.getDescription())
                        .setPostingDate(job.getPostingDate().toString())
                        .setDeadline(job.getDeadline().toString())
                        .setLocation(job.getLocation())
                        .setType(job.getType().toString())
                        .setSalary(job.getSalary())
                        .setStatus(job.getStatus().toString())
                        .setJobProvider(JobProviderProto.newBuilder()
                                .setId(job.getJobProvider().getId())
                                .setEmail(job.getJobProvider().getEmail())
                                .setName(job.getJobProvider().getName())
                                .setDescription(job.getJobProvider().getDescription())
                                .setPhoneNumber(job.getJobProvider().getPhoneNumber()))
                        .build())
                .toList();

        String nextPageToken = pageToken < jobs.getTotalPages() - 1 ? String.valueOf(pageToken + 1) : "";

        ListJobsResponse response = ListJobsResponse
                .newBuilder()
                .addAllJobs(jobMessages)
                .setNextPageToken(nextPageToken)
                .setTotalSize((int) jobs.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getJob(GetJobRequest request, StreamObserver<JobProto> responseObserver) {
        log.info("GetJobRequest: {}", request);

        Job job = jobRepository.findById(request.getId()).orElseThrow();

        JobProto response = JobProto.newBuilder()
                .setId(job.getId())
                .setTitle(job.getTitle())
                .setDescription(job.getDescription())
                .setPostingDate(job.getPostingDate().toString())
                .setDeadline(job.getDeadline().toString())
                .setLocation(job.getLocation())
                .setType(job.getType().toString())
                .setSalary(job.getSalary())
                .setStatus(job.getStatus().toString())
                .setJobProvider(JobProviderProto.newBuilder()
                        .setId(job.getJobProvider().getId())
                        .setEmail(job.getJobProvider().getEmail())
                        .setName(job.getJobProvider().getName())
                        .setDescription(job.getJobProvider().getDescription())
                        .setPhoneNumber(job.getJobProvider().getPhoneNumber()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
