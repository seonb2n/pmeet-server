package pmeet.pmeetserver.user.service.job

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.dto.job.request.CreateJobRequestDto
import pmeet.pmeetserver.user.dto.job.response.JobResponseDto

@Service
class JobFacadeService(
  private val jobService: JobService
) {

  @Transactional
  suspend fun createJob(requestDto: CreateJobRequestDto): JobResponseDto {
    val job = Job(
      name = requestDto.name
    )
    return JobResponseDto.from(jobService.save(job))
  }

  @Transactional(readOnly = true)
  suspend fun searchJobByName(name: String?, pageable: Pageable): Slice<JobResponseDto> {
    return jobService.searchByJobName(name, pageable).map { JobResponseDto.from(it) }
  }
}
