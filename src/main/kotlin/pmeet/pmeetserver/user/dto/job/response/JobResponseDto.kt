package pmeet.pmeetserver.user.dto.job.response

import pmeet.pmeetserver.user.domain.job.Job

data class JobResponseDto(
  val id: String,
  val name: String
) {
  companion object {
    fun from(job: Job): JobResponseDto {
      return JobResponseDto(
        id = job.id!!,
        name = job.name
      )
    }
  }
}
