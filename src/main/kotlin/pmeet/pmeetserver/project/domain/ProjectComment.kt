package pmeet.pmeetserver.project.domain

import java.time.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class ProjectComment(
  @Id
  val id: String? = null,
  val parentCommentId: String? = null,
  val projectId: String, // 프로젝트 ID 참조
  val userId: String,
  var content: String,
  var liker: List<String> = mutableListOf(), // 좋아요를 한 user id list
  val createdAt: LocalDateTime = LocalDateTime.now(),
)