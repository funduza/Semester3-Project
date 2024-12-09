package sep3.project.jobservice;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.entities.JobApplication;
import sep3.project.jobservice.entities.JobProvider;
import sep3.project.jobservice.entities.JobSeeker;
import sep3.project.jobservice.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class JobServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner seedDatabase(JobRepository jobRepository, JobProviderRepository jobProviderRepository, JobSeekerRepository jobSeekerRepository, JobApplicationRepository jobApplicationRepository) {
        return args -> {
            List<JobSeeker> jobSeekers = new ArrayList<>();
            List<JobProvider> jobProviders = new ArrayList<>();
            List<Job> jobs = new ArrayList<>();
            List<JobApplication> jobApplications = new ArrayList<>();

            Faker faker = new Faker(new Locale("da-DK"), new Random(42));

            for (int i = 0; i < 10; i++) {
                JobSeeker jobSeeker = JobSeeker.newBuilder()
                        .setEmail(faker.internet().emailAddress())
                        .setPassword(faker.internet().password())
                        .setFirstName(faker.name().firstName())
                        .setLastName(faker.name().lastName())
                        .setPhoneNumber(faker.phoneNumber().phoneNumber())
                        .setResume(faker.lorem().paragraph())
                        .build();

                jobSeekers.add(jobSeeker);
            }

            List<JobSeeker> savedJobSeekers = jobSeekerRepository.saveAll(jobSeekers);

            for (int i = 0; i < 10; i++) {
                JobProvider jobProvider = JobProvider.newBuilder()
                        .setEmail(faker.internet().emailAddress())
                        .setPassword(faker.internet().password())
                        .setName(faker.company().name())
                        .setDescription(faker.company().catchPhrase())
                        .setPhoneNumber(faker.phoneNumber().phoneNumber())
                        .build();

                jobProviders.add(jobProvider);
            }

            List<JobProvider> savedJobProviders = jobProviderRepository.saveAll(jobProviders);

            for (int i = 0; i < 500; i++) {
                Job job = Job.newBuilder()
                        .setTitle(faker.job().title())
                        .setDescription(String.join("\n", faker.lorem().paragraphs(faker.number().numberBetween(3, 7))))
                        .setPostingDate(faker.date().past(100, TimeUnit.DAYS))
                        .setDeadline(faker.date().future(100, TimeUnit.DAYS))
                        .setLocation(faker.address().fullAddress())
                        .setType(Job.Type.values()[faker.random().nextInt(Job.Type.values().length)])
                        .setSalary(faker.number().randomDouble(2, 5000, 50000))
                        .setStatus(Job.Status.values()[faker.random().nextInt(Job.Status.values().length)])
                        .setJobProvider(savedJobProviders.get(faker.random().nextInt(jobProviders.size())))
                        .build();
                jobs.add(job);
            }

            List<Job> savedJobs = jobRepository.saveAll(jobs);

            for (int i = 0; i < 10; i++) {
                JobApplication jobApplication = JobApplication.newBuilder()
                        .setJob(savedJobs.get(faker.random().nextInt(jobs.size())))
                        .setJobSeeker(savedJobSeekers.get(faker.random().nextInt(savedJobSeekers.size())))
                        .setStatus(JobApplication.Status.values()[faker.random().nextInt(JobApplication.Status.values().length)])
                        .build();
                jobApplications.add(jobApplication);
            }

            jobApplicationRepository.saveAll(jobApplications);
        };
    }
}
