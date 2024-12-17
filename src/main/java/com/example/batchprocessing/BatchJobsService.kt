package com.example.batchprocessing

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.stereotype.Service

@Service
class BatchJobsService(
    private val jobLauncher: JobLauncher,
    private val jobRegistry: JobRegistry
) {

    fun launchJobByName(jobName: String): JobExecution {
        val job: Job = jobRegistry.getJob(jobName)
        return jobLauncher.run(job, JobParameters(mapOf("test" to JobParameter("test2", String::class.java, true))))
    }
}
