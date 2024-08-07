package pmeet.pmeetserver.user.service.resume

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pmeet.pmeetserver.common.ErrorCode
import pmeet.pmeetserver.common.exception.ForbiddenRequestException
import pmeet.pmeetserver.user.dto.resume.request.ChangeResumeActiveRequestDto
import pmeet.pmeetserver.user.dto.resume.request.CopyResumeRequestDto
import pmeet.pmeetserver.user.dto.resume.request.CreateResumeRequestDto
import pmeet.pmeetserver.user.dto.resume.request.DeleteResumeRequestDto
import pmeet.pmeetserver.user.dto.resume.request.UpdateResumeRequestDto
import pmeet.pmeetserver.user.dto.resume.response.ResumeResponseDto

@Service
class ResumeFacadeService(
  private val resumeService: ResumeService
) {

  @Transactional
  suspend fun createResume(requestDto: CreateResumeRequestDto): ResumeResponseDto {
    val resume = requestDto.toEntity()
    return ResumeResponseDto.from(resumeService.save(resume))
  }

  @Transactional(readOnly = true)
  suspend fun findResumeById(resumeId: String): ResumeResponseDto {
    return ResumeResponseDto.from(resumeService.getByResumeId(resumeId))
  }

  @Transactional(readOnly = true)
  suspend fun findResumeListByUserId(userId: String): List<ResumeResponseDto> {
    return resumeService.getAllByUserId(userId).map { ResumeResponseDto.from(it) }
  }

  @Transactional
  suspend fun updateResume(userId: String, requestDto: UpdateResumeRequestDto): ResumeResponseDto {
    val originalResume = resumeService.getByResumeId(requestDto.id);
    if (!originalResume.userId.equals(userId)) {
      throw ForbiddenRequestException(ErrorCode.RESUME_UPDATE_FORBIDDEN)
    }
    val updateResume = originalResume.update(
      title = requestDto.title,
      userProfileImageUrl = requestDto.userProfileImageUrl,
      desiredJobs = requestDto.desiredJobs.map { it.toEntity() },
      techStacks = requestDto.techStacks.map { it.toEntity() },
      jobExperiences = requestDto.jobExperiences.map { it.toEntity() },
      projectExperiences = requestDto.projectExperiences.map { it.toEntity() },
      portfolioFileUrl = requestDto.portfolioFileUrl,
      portfolioUrl = requestDto.portfolioUrl,
      selfDescription = requestDto.selfDescription
    )
    return ResumeResponseDto.from(resumeService.update(updateResume))
  }

  @Transactional
  suspend fun deleteResume(requestDto: DeleteResumeRequestDto) {
    val originalResume = resumeService.getByResumeId(requestDto.id);
    if (!originalResume.userId.equals(requestDto.userId)) {
      throw ForbiddenRequestException(ErrorCode.RESUME_DELETE_FORBIDDEN)
    }
    resumeService.delete(originalResume)
  }

  @Transactional
  suspend fun copyResume(userId: String, requestDto: CopyResumeRequestDto): ResumeResponseDto{
    val originalResume = resumeService.getByResumeId(requestDto.id);
    if (!originalResume.userId.equals(userId)) {
      throw ForbiddenRequestException(ErrorCode.RESUME_COPY_FORBIDDEN)
    }
    return ResumeResponseDto.from(resumeService.save(originalResume.copy()))
  }

  @Transactional
  suspend fun changeResumeActiveStatus(userId: String, requestDto: ChangeResumeActiveRequestDto) {
    val originalResume = resumeService.getByResumeId(requestDto.id)
    if (!originalResume.userId.equals(userId)) {
      throw ForbiddenRequestException(ErrorCode.RESUME_ACTIVE_CHANGE_FORBIDDEN)
    }
    resumeService.changeActive(originalResume, requestDto.targetActiveStatus)
  }
}