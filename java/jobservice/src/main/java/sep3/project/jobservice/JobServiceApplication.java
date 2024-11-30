package sep3.project.jobservice;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sep3.project.jobservice.entities.Job;
import sep3.project.jobservice.repositories.JobRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class JobServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDatabase(JobRepository jobRepository) {
        return args -> {
            List<Job> jobs = new ArrayList<>();
            Faker faker = new Faker(new Locale("da-DK"), new Random(42));

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
                        .build();
                jobs.add(job);
            }

            jobRepository.saveAll(jobs);
        };
    }
}
