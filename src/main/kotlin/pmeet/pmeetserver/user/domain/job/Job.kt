package pmeet.pmeetserver.user.domain.job

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Job(
  @Id
  val id: String? = null,
  val name: String
) {
}
