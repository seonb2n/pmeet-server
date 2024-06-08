package pmeet.pmeetserver.user.controller

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pmeet.pmeetserver.user.dto.job.request.CreateJobRequestDto
import pmeet.pmeetserver.user.dto.job.response.JobResponseDto
import pmeet.pmeetserver.user.service.job.JobFacadeService
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
  private val jobFacadeService: JobFacadeService
) {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  suspend fun createJob(
    @AuthenticationPrincipal userId: Mono<String>,
    @RequestBody requestDto: CreateJobRequestDto
  ): JobResponseDto {
    return jobFacadeService.createJob(requestDto)
  }
}
