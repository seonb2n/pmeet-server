package pmeet.pmeetserver.user.service.job

import org.springframework.stereotype.Service
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.dto.job.request.CreateJobRequestDto
import pmeet.pmeetserver.user.dto.job.response.JobResponseDto

@Service
class JobFacadeService(
  private val jobService: JobService
) {
  suspend fun createJob(requestDto: CreateJobRequestDto): JobResponseDto {
    val job = Job(
      name = requestDto.name
    )
    return JobResponseDto.from(jobService.save(job))
  }
}
