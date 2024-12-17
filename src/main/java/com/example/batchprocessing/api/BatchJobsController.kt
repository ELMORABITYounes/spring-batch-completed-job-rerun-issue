package com.example.batchprocessing.api

import com.example.batchprocessing.BatchJobsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = [BatchJobsController.endpointPath],
)
class BatchJobsController(
    private val batchJobsService: BatchJobsService,
) {

    companion object {
        const val endpointPath = "/batch-jobs"
    }

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/{job_name}")
    fun launchJobByName(@PathVariable(name = "job_name") jobName: String): ResponseEntity<String> {
        logger.info("Got request to trigger batch job: $jobName.")
        return try {
            val jobExecution = batchJobsService.launchJobByName(jobName)
            ResponseEntity.ok("job launched successfully with status: ${jobExecution.status}")
        } catch (e: NoSuchJobException) {
            logger.info("no job with id $jobName found.")
            ResponseEntity.notFound().build()
        } catch (e: JobExecutionAlreadyRunningException) {
            logger.warn("job with id $jobName already running.", e)
            ResponseEntity.status(HttpStatus.CONFLICT).body("job already running")
        } catch (e: JobRestartException) {
            logger.error("job with id $jobName could not be restarted.", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } catch (e: JobInstanceAlreadyCompleteException) {
            logger.info("job with id $jobName already complete.", e)
            ResponseEntity.ok("job already completed")
        } catch (e: JobParametersInvalidException) {
            logger.warn("params for job with id $jobName are not valid.", e)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
}
