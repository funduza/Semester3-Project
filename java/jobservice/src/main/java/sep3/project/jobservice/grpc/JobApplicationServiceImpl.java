package sep3.project.jobservice.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import sep3.project.applicationservice.grpc.CreateJobApplicationRequest;
import sep3.project.applicationservice.grpc.JobApplicationProto;
import sep3.project.applicationservice.grpc.JobApplicationServiceGrpc;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobApplication;
import sep3.project.jobservice.entities.JobSeeker;
import sep3.project.jobservice.repositories.JobApplicationRepository;
import sep3.project.jobservice.repositories.JobProviderRepository;
import sep3.project.jobservice.repositories.JobRepository;
import sep3.project.jobservice.repositories.JobSeekerRepository;

@Slf4j
@GrpcService
public class JobApplicationServiceImpl extends JobApplicationServiceGrpc.JobApplicationServiceImplBase {

    private final JobApplicationRepository jobApplicationRepository;

    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository, JobProviderRepository jobProviderRepositor,
                                     JobRepository jobRepository, JobSeekerRepository jobSeekerRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    public void createJobApplication(CreateJobApplicationRequest request, StreamObserver<JobApplicationProto> responseObserver) {
        log.info("CreateJobApplicationRequest: {}", request);

        JobApplicationProto jobApplicationProto = request.getJobApplication();
        JobApplicationProto response = null;
        JobSeeker jobSeeker = jobSeekerRepository.getReferenceById(jobApplicationProto.getJobSeekerId());
        Job job = jobRepository.getReferenceById(jobApplicationProto.getJobId());

        JobApplication jobApplication = JobApplication.newBuilder()
                .setJobSeeker(jobSeeker)
                .setJob(job)
                .setStatus(JobApplication.Status.InProgress)
                .build();

        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        response = JobApplicationProto.newBuilder()
                .setId(savedJobApplication.getId())
                .setJobSeekerId(savedJobApplication.getJobSeeker().getId())
                .setJobId(savedJobApplication.getJob().getId())
                .setStatus(savedJobApplication.getStatus().toString())
                .setApplicationDate(savedJobApplication.getApplicationDate().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

