package pmeet.pmeetserver.user.job.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.springframework.test.util.ReflectionTestUtils
import pmeet.pmeetserver.user.domain.job.Job
import pmeet.pmeetserver.user.dto.job.request.CreateJobRequestDto
import pmeet.pmeetserver.user.service.job.JobFacadeService
import pmeet.pmeetserver.user.service.job.JobService

@ExperimentalCoroutinesApi
internal class JobFacadeServiceUnitTest : DescribeSpec({

  val testDispatcher = StandardTestDispatcher()

  val jobService = mockk<JobService>(relaxed = true)
  lateinit var jobFacadeService: JobFacadeService

  lateinit var job: Job

  beforeSpec {
    Dispatchers.setMain(testDispatcher)
    jobFacadeService = JobFacadeService(jobService)

    job = Job(
      name = "Software Engineer",
    )
    ReflectionTestUtils.setField(job, "id", "testId")
  }

  afterSpec {
    Dispatchers.resetMain()
  }

  describe("cratedJob") {
    context("createJobRequestDto가 주어지면") {
      val requestDto = CreateJobRequestDto(
        name = "test"
      )
      it("JobResponseDto를 반환한다") {
        runTest {
          coEvery { jobService.save(any()) } answers { job }
          val result = jobFacadeService.createJob(requestDto)

          result.name shouldBe job.name
        }
      }
    }
  }

})
