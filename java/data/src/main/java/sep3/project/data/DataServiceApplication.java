package sep3.project.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sep3.project.data.entities.Job;
import sep3.project.data.repositories.JobRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class DataServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDatabase(JobRepository jobRepository) {
        return args -> {
            List<Job> jobs = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                Job job = Job.newBuilder()
                        .setTitle("Job " + i)
                        .setDescription("Job  description " + i)
                        .setPostingDate(new Date())
                        .setDeadline(new Date())
                        .setLocation("Location " + i)
                        .setType(i % 2 == 0 ? Job.Type.FullTime : Job.Type.PartTime)
                        .setSalary(new Random().nextDouble() * 10000)
                        .setStatus(i % 2 == 0 ? Job.Status.Active : Job.Status.Closed)
                        .build();
                jobs.add(job);
            }

            jobRepository.saveAll(jobs);
        };
    }
}
