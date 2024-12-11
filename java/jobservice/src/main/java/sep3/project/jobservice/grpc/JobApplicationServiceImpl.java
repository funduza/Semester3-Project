package sep3.project.jobservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobApplication;
import sep3.project.jobservice.entities.JobSeeker;
import sep3.project.jobservice.repositories.JobApplicationRepository;
import sep3.project.jobservice.repositories.JobRepository;
import sep3.project.jobservice.repositories.JobSeekerRepository;

@Slf4j
@GrpcService
public class JobApplicationServiceImpl extends JobApplicationServiceGrpc.JobApplicationServiceImplBase {
    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobApplicationServiceImpl(
            JobApplicationRepository jobApplicationRepository,
            JobRepository jobRepository,
            JobSeekerRepository jobSeekerRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    public void createJobApplication(CreateJobApplicationRequest request, StreamObserver<JobApplicationProto> responseObserver) {
        log.info("CreateJobApplicationRequest: {}", request);

        JobApplicationProto jobApplicationProto = request.getJobApplication();
        Job job = jobRepository.findById(jobApplicationProto.getJobId()).orElseThrow();
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobApplicationProto.getJobSeekerId()).orElseThrow();

        JobApplication jobApplication = JobApplication.newBuilder()
                .setStatus(JobApplication.Status.InProgress)
                .setJob(job)
                .setJobSeeker(jobSeeker)
                .build();

        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        JobApplicationProto response = JobApplicationProto.newBuilder()
                .setId(savedJobApplication.getId())
                .setStatus(savedJobApplication.getStatus().toString())
                .setApplicationDate(savedJobApplication.getApplicationDate().toString())
                .setJobId(savedJobApplication.getJob().getId())
                .setJobSeekerId(savedJobApplication.getJobSeeker().getId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
