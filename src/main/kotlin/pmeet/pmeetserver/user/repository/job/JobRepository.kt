package pmeet.pmeetserver.user.repository.job

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import pmeet.pmeetserver.user.domain.job.Job
import reactor.core.publisher.Mono

interface JobRepository : ReactiveMongoRepository<Job, String> {

  fun findByName(name: String): Mono<Job>
}
