package pmeet.pmeetserver.user.service.job

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pmeet.pmeetserver.common.ErrorCode
import pmeet.pmeetserver.common.exception.EntityDuplicateException
import pmeet.pmeetserver.common.utils.page.SliceResponse
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

  @Transactional(readOnly = true)
  suspend fun searchByJobName(name: String?, pageable: Pageable): Slice<Job> {
    return SliceResponse.of(
      jobRepository.findByNameSearchSlice(name, pageable).collectList().awaitSingle(),
      pageable
    )
  }
}

