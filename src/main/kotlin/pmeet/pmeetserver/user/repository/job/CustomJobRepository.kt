package pmeet.pmeetserver.user.repository.job

import org.springframework.data.domain.Pageable
import pmeet.pmeetserver.user.domain.job.Job
import reactor.core.publisher.Flux

interface CustomJobRepository {

  fun findByNameSearchSlice(name: String?, pageable: Pageable): Flux<Job>
}
