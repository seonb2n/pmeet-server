package pmeet.pmeetserver.user.repository.job

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import pmeet.pmeetserver.user.domain.job.Job
import reactor.core.publisher.Flux

class CustomJobRepositoryImpl(
  @Autowired private val mongoTemplate: ReactiveMongoTemplate
) : CustomJobRepository {

  override fun findByNameSearchSlice(name: String?, pageable: Pageable): Flux<Job> {
    val criteria = if (name != null) {
      Criteria.where("name").regex(".*${name}.*")
    } else {
      Criteria()
    }

    val match = Aggregation.match(criteria)

    val addFields = Aggregation.project("name")
      .andExpression("strLenCP(name)").`as`("nameLength")

    val sort = Aggregation.sort(
      Sort.by(
        Sort.Order(Sort.Direction.ASC, "nameLength")
      ).and(
        Sort.by(Sort.Direction.ASC, "name")
      )
    )

    val limit = Aggregation.limit(pageable.pageSize.toLong() + 1)
    val skip = Aggregation.skip((pageable.pageNumber * pageable.pageSize).toLong())

    val aggregation = Aggregation.newAggregation(match, addFields, sort, skip, limit)

    return mongoTemplate.aggregate(aggregation, "job", Job::class.java)
  }
}
