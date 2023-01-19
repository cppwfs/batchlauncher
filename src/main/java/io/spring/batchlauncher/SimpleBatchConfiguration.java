package io.spring.batchlauncher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@EnableBatchProcessing
@Configuration
public class SimpleBatchConfiguration {

    private static final Log logger = LogFactory.getLog(SimpleBatchConfiguration.class);

    public SimpleBatchConfiguration(DataSource dataSource, JobLauncher jobLauncher, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("/org/springframework/batch/core/schema-h2.sql"));
        databasePopulator.execute(dataSource);
        this.jobLauncher = jobLauncher;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    JobLauncher jobLauncher;

    JobRepository jobRepository;

    PlatformTransactionManager transactionManager;

    @Bean
    public Job job1() {
        return new JobBuilder("job1", this.jobRepository)
                .start(new StepBuilder("job1step1", this.jobRepository).tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
                            throws Exception {
                        logger.info("Job1 was run");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager).build()).incrementer(new RunIdIncrementer()).build();
    }

    @Bean
    public Job job2() {
        return new JobBuilder("job2", this.jobRepository)
                .start(new StepBuilder("job1step1", this.jobRepository).tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
                            throws Exception {
                        logger.info("Job2 was run");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager).build()).incrementer(new RunIdIncrementer()).build();
    }
}
