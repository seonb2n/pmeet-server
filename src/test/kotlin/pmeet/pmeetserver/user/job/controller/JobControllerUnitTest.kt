package pmeet.pmeetserver.user.job.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pmeet.pmeetserver.config.TestSecurityConfig
import pmeet.pmeetserver.user.controller.JobController
import pmeet.pmeetserver.user.dto.job.request.CreateJobRequestDto
import pmeet.pmeetserver.user.dto.job.response.JobResponseDto
import pmeet.pmeetserver.user.service.job.JobFacadeService

@WebFluxTest(JobController::class)
@Import(TestSecurityConfig::class)
internal class JobControllerUnitTest : DescribeSpec() {

  @Autowired
  private lateinit var webTestClient: WebTestClient

  @MockkBean
  lateinit var jobFacadeService: JobFacadeService

  init {
    describe("POST api/v1/jobs") {
      context("인증된 유저의 직무 생성 요청이 들어오면") {
        val userId = "1234"
        val requestDto = CreateJobRequestDto("TestJob")
        val responseDto = JobResponseDto("1234", "TestJob")
        coEvery { jobFacadeService.createJob(requestDto) } answers { responseDto }
        val mockAuthentication = UsernamePasswordAuthenticationToken(userId, null, null)
        val performRequest =
          webTestClient
            .mutateWith(mockAuthentication(mockAuthentication))
            .post()
            .uri("/api/v1/jobs")
            .bodyValue(requestDto)
            .exchange()

        it("서비스를 통해 데이터를 생성한다") {
          coVerify(exactly = 1) { jobFacadeService.createJob(requestDto) }
        }

        it("요청은 성공한다") {
          performRequest.expectStatus().isCreated
        }

        it("생성된 직무 정보를 반환한다") {
          performRequest.expectBody<JobResponseDto>().consumeWith { response ->
            response.responseBody?.id shouldBe responseDto.id
            response.responseBody?.name shouldBe responseDto.name
          }
        }
      }
      context("인증되지 않은 유저의 직무 생성 요청이 들어오면") {
        val requestDto = CreateJobRequestDto("TestJob")
        val performRequest =
          webTestClient
            .post()
            .uri("/api/v1/jobs")
            .bodyValue(requestDto)
            .exchange()
        it("요청은 실패한다") {
          performRequest.expectStatus().isUnauthorized
        }
      }
    }
  }
}
