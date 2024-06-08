package pmeet.pmeetserver.user.job.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pmeet.pmeetserver.common.ErrorCode
import pmeet.pmeetserver.common.exception.EntityDuplicateException
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.repository.job.JobRepository
import pmeet.pmeetserver.user.service.job.JobService
import reactor.core.publisher.Mono

@ExperimentalCoroutinesApi
internal class JobServiceUnitTest : DescribeSpec({

  val testDispatcher = StandardTestDispatcher()

  val jobRepository = mockk<JobRepository>(relaxed = true)
  lateinit var jobService: JobService
  lateinit var job: Job

  beforeSpec {
    Dispatchers.setMain(testDispatcher)
    jobService = JobService(jobRepository)

    job = Job(
      name = "testName",
    )
  }

  afterSpec {
    Dispatchers.resetMain()
  }

  describe("save") {
    context("직무 정보가 주어지면") {
      it("저장 후 직무를 반환한다") {
        runTest {
          every { jobRepository.save(any()) } answers { Mono.just(job) }
          every { jobRepository.findByName(job.name) } answers { Mono.empty() }

          val result = jobService.save(job)

          result.name shouldBe job.name
        }
      }
    }

    context("이미 존재하는 직무 이름이 주어지면") {
      every { jobRepository.findByName(job.name) } answers { Mono.just(job) }
      it("EntityDuplicateException을 던진다") {
        runTest {
          val exception = shouldThrow<EntityDuplicateException> {
            jobService.save(job)
          }

          exception.errorCode shouldBe ErrorCode.JOB_DUPLICATE_BY_NAME
        }
      }
    }
  }
})
