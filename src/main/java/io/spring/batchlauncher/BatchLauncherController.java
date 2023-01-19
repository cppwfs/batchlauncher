package io.spring.batchlauncher;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class BatchLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job1;

    @Autowired
    Job job2;


    @RequestMapping("/launchbatch")
    public String  launchBatchApp(String jobId) throws Exception{
        String result = "No Job Executed";
        if (jobId != null) {
            if (jobId.equals("job1")) {
                result = jobLauncher.run(job1, new JobParameters()).toString();
            }
            if (jobId.equals("job2")) {
                result = jobLauncher.run(job2, new JobParameters()).toString();
            }
        }
        return result;

    }
}
