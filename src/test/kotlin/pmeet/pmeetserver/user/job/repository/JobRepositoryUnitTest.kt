package pmeet.pmeetserver.user.job.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import pmeet.pmeetserver.config.MongoTestConfig
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.repository.job.JobRepository

@ExperimentalCoroutinesApi
@ContextConfiguration(classes = [MongoTestConfig::class])
internal class JobRepositoryUnitTest(
  @Autowired @Qualifier("testMongoTemplate") private val template: ReactiveMongoTemplate
) : DescribeSpec({

  val testDispatcher = StandardTestDispatcher()

  val factory = ReactiveMongoRepositoryFactory(template)
  val jobRepository = factory.getRepository(JobRepository::class.java)

  lateinit var job: Job

  beforeSpec {
    job = Job(
      name = "testName",
    )
    jobRepository.save(job).block()

    Dispatchers.setMain(testDispatcher)
  }

  afterSpec {
    Dispatchers.resetMain()
    jobRepository.deleteAll().block()
  }

  describe("findByName") {
    context("직무 이름이 주어지면") {
      it("해당하는 이름의 직무를 반환한다") {
        runTest {

          val result = jobRepository.findByName(job.name).block()

          result?.name shouldBe job.name
        }
      }
    }
  }

}) {
  companion object {
    @Container
    val mongoDBContainer = MongoDBContainer("mongo:latest").apply {
      withExposedPorts(27017)
      start()
    }

    init {
      System.setProperty(
        "spring.data.mongodb.uri",
        "mongodb://localhost:${mongoDBContainer.getMappedPort(27017)}/test"
      )
    }
  }
}

