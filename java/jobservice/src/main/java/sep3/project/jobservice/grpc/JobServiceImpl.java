package sep3.project.jobservice.grpc;

import io.github.perplexhub.rsql.RSQLJPASupport;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.repositories.JobProviderRepository;
import sep3.project.jobservice.repositories.JobRepository;

import java.time.LocalDateTime;
import java.util.List;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@GrpcService
public class JobServiceImpl extends JobServiceGrpc.JobServiceImplBase {
    private final JobRepository jobRepository;
    private final JobProviderRepository jobProviderRepository;

    public JobServiceImpl(JobRepository jobRepository,
        JobProviderRepository jobProviderRepository) {
        this.jobRepository = jobRepository;
        this.jobProviderRepository = jobProviderRepository;
    }

    @Override
    public void listJobs(ListJobsRequest request, StreamObserver<ListJobsResponse> responseObserver) {
        log.info("ListJobsRequest: {}", request);

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
                .setJobProvider(UserProto.newBuilder()
                    .setId(job.getJobProvider().getId())
                    .setEmail(job.getJobProvider().getEmail())
                    .setJobProvider(JobProviderProto.newBuilder()
                        .setName(job.getJobProvider().getName())
                        .setDescription(job.getJobProvider().getDescription())
                        .setPhoneNumber(job.getJobProvider().getPhoneNumber()))
                    .build())
                .build()
            )
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
            .setJobProvider(UserProto.newBuilder()
                .setId(job.getJobProvider().getId())
                .setEmail(job.getJobProvider().getEmail())
                .setJobProvider(JobProviderProto.newBuilder()
                    .setName(job.getJobProvider().getName())
                    .setDescription(job.getJobProvider().getDescription())
                    .setPhoneNumber(job.getJobProvider().getPhoneNumber()))
                .build())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createJob(CreateJobRequest request, StreamObserver<JobProto> responseObserver) {
        log.info("CreateJobRequest received: {}", request);

        JobProto jobProto = request.getJob();
        JobProvider jobProvider = jobProviderRepository.findById(jobProto.getJobProvider().getId()).orElseThrow();
        OffsetDateTime deadlineDateTime = OffsetDateTime.parse(jobProto.getDeadline(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Job job = Job.newBuilder()
            .setTitle(jobProto.getTitle())
            .setDescription(jobProto.getDescription())
            .setPostingDate(new Date())
            .setDeadline(Date.from(deadlineDateTime.toInstant()))
            .setLocation(jobProto.getLocation())
            .setType(Job.Type.valueOf(jobProto.getType()))
            .setStatus(Job.Status.Active)
            .setSalary(jobProto.getSalary())
            .setJobProvider(jobProvider)
            .build();

        Job savedJob = jobRepository.save(job);

        JobProto responseJobProto = JobProto.newBuilder()
            .setId(savedJob.getId())
            .setTitle(savedJob.getTitle())
            .setDescription(savedJob.getDescription())
            .setPostingDate(savedJob.getPostingDate().toInstant().toString())
            .setDeadline(savedJob.getDeadline().toInstant().toString())
            .setLocation(savedJob.getLocation())
            .setType(savedJob.getType().toString())
            .setSalary(savedJob.getSalary())
            .setStatus(savedJob.getStatus().toString())
            .setJobProvider(UserProto.newBuilder()
                .setEmail(savedJob.getJobProvider().getEmail())
                .setJobProvider(JobProviderProto.newBuilder()
                    .setName(job.getJobProvider().getName())
                    .build())
                .build()
            )
            .build();

        responseObserver.onNext(responseJobProto);
        responseObserver.onCompleted();
    }
}