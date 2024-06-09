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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
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
      name = "testName",
    )
    ReflectionTestUtils.setField(job, "id", "testId")
  }

  afterSpec {
    Dispatchers.resetMain()
  }

  describe("cratedJob") {
    context("createJobRequestDto가 주어지면") {
      val requestDto = CreateJobRequestDto(
        name = "testName"
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

  describe("searchJobByName") {
    context("직무 이름과 페이징 정보가 주어지면") {
      val name = "Test"
      val pageNumber = 0
      val pageSize = 10
      val jobs = mutableListOf<Job>()
      for (i in 1..pageSize * 2) {
        jobs.add(Job(name = name + i))
        ReflectionTestUtils.setField(jobs[i - 1], "id", "testId$i")
      }
      it("이름을 포함하는 직무들을 Slice로 반환한다") {
        runTest {
          coEvery { jobService.searchByJobName(name, any()) } answers {
            SliceImpl(
              jobs.subList(0, pageSize),
              PageRequest.of(pageNumber, pageSize),
              true
            )
          }
          val result = jobFacadeService.searchJobByName(name, PageRequest.of(pageNumber, pageSize))

          result.size shouldBe pageSize
          result.isFirst shouldBe true
          result.isLast shouldBe false
          result.hasNext() shouldBe true
          result.forEachIndexed { index, jobResponseDto ->
            jobResponseDto.name shouldBe name + (index + 1)
          }
        }
      }
    }
  }

})
