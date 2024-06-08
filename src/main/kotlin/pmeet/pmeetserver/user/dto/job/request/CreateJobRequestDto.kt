package pmeet.pmeetserver.user.dto.job.request

import jakarta.validation.constraints.NotBlank

data class CreateJobRequestDto(
  @field:NotBlank(message = "직무를 입력해 주세요.")
  val name: String
)
