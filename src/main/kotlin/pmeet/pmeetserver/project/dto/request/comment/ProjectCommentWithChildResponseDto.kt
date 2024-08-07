package pmeet.pmeetserver.project.dto.request.comment

import java.time.LocalDateTime
import pmeet.pmeetserver.project.domain.ProjectComment

data class ProjectCommentWithChildResponseDto(
  val id: String,
  val parentCommentId: String?,
  val projectId: String,
  val userId: String,
  val content: String,
  val likerIdList: List<String>,
  val createdAt: LocalDateTime,
  val isDeleted: Boolean,
  val childComments: List<ProjectCommentResponseDto>
) {
  companion object {
    fun from(comment: ProjectComment, childComments: List<ProjectComment>): ProjectCommentWithChildResponseDto {
      return ProjectCommentWithChildResponseDto(
        id = comment.id!!,
        parentCommentId = comment.parentCommentId,
        projectId = comment.projectId,
        userId = comment.userId,
        content = comment.content,
        likerIdList = comment.likerIdList,
        createdAt = comment.createdAt,
        isDeleted = comment.isDeleted,
        childComments = childComments.map { ProjectCommentResponseDto.from(it) }
      )
    }
  }
}
