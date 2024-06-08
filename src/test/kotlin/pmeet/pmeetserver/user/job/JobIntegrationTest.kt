package pmeet.pmeetserver.user.job

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.dto.job.request.CreateJobRequestDto
import pmeet.pmeetserver.user.dto.job.response.JobResponseDto
import pmeet.pmeetserver.user.repository.job.JobRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExperimentalCoroutinesApi
internal class JobIntegrationTest : DescribeSpec() {

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var jobRepository: JobRepository

  val job = Job(
    name = "testName",
  )

  override suspend fun beforeSpec(spec: Spec) {
    withContext(Dispatchers.IO) {
      jobRepository.save(job).block()
    }
  }

  override suspend fun afterSpec(spec: Spec) {
    withContext(Dispatchers.IO) {
      jobRepository.deleteAll().block()
    }
  }

  init {
    describe("POST /api/v1/jobs") {
      context("인증된 유저의 직무 생성 요청이 들어오면") {
        val userId = "1234"
        val mockAuthentication = UsernamePasswordAuthenticationToken(userId, null, null)
        val requestDto = CreateJobRequestDto("TestJob")

        val performRequest = webTestClient
          .mutateWith(mockAuthentication(mockAuthentication))
          .post()
          .uri("/api/v1/jobs")
          .accept(MediaType.APPLICATION_JSON)
          .bodyValue(requestDto)
          .exchange()

        it("요청은 성공한다") {
          performRequest.expectStatus().isCreated
        }

        it("생성된 직무 정보를 반환한다") {
          performRequest.expectBody<JobResponseDto>().consumeWith { response ->
            response.responseBody?.name shouldBe requestDto.name
          }
        }
      }
    }
  }

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
