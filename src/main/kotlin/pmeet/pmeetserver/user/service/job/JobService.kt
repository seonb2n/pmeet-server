package pmeet.pmeetserver.user.service.job

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pmeet.pmeetserver.common.ErrorCode
import pmeet.pmeetserver.common.exception.EntityDuplicateException
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.repository.job.JobRepository

@Service
class JobService(
  private val jobRepository: JobRepository
) {

  @Transactional
  suspend fun save(job: Job): Job {
    jobRepository.findByName(job.name).awaitSingleOrNull()
      ?.let { throw EntityDuplicateException(ErrorCode.JOB_DUPLICATE_BY_NAME) }
    return jobRepository.save(job).awaitSingle()
  }
}
