package pmeet.pmeetserver.user

import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pmeet.pmeetserver.config.BaseMongoDBTestForIntegration
import pmeet.pmeetserver.user.domain.User
import pmeet.pmeetserver.user.domain.enum.Gender
import pmeet.pmeetserver.user.dto.request.UpdateUserRequestDto
import pmeet.pmeetserver.user.dto.response.UserResponseDto
import pmeet.pmeetserver.user.dto.response.UserSummaryResponseDto
import pmeet.pmeetserver.user.repository.UserRepository
import java.time.LocalDate


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExperimentalCoroutinesApi
@ActiveProfiles("test")
internal class UserIntegrationTest : BaseMongoDBTestForIntegration() {

  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  lateinit var userRepository: UserRepository

  private val user = User(
    email = "test@example.com",
    name = "testUser",
    nickname = "testNickname",
    password = "password",
    phoneNumber = "1234567890",
    gender = Gender.MALE,
    introductionComment = "testIntroduction",
    profileImageUrl = "http://test.image.url"
  )

  lateinit var userId: String

  override suspend fun beforeSpec(spec: Spec) {
    withContext(Dispatchers.IO) {
      userRepository.save(user).block()
      userId = user.id!!
    }
  }

  override suspend fun afterSpec(spec: Spec) {
    withContext(Dispatchers.IO) {
      userRepository.deleteAll().block()
    }
  }

  init {
    describe("GET /api/v1/users/me/summary") {
      context("인증된 사용자의 요약 정보를 가져올 때") {
        it("사용자의 요약 정보를 반환한다") {
          val mockAuthentication = UsernamePasswordAuthenticationToken(userId, null, null)

          webTestClient.mutateWith(mockAuthentication(mockAuthentication)).get()
            .uri("/api/v1/users/me/summary")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<UserSummaryResponseDto>()
            .consumeWith {
              val userSummary = it.responseBody!!
              userSummary.email shouldBe user.email
              userSummary.nickname shouldBe user.nickname
              userSummary.isEmployed shouldBe user.isEmployed
              userSummary.profileImageUrl shouldNotBe user.profileImageUrl
            }
        }
      }
    }

    describe("GET /api/v1/users/me") {
      context("인증된 사용자의 정보를 가져올 때") {
        it("사용자의 정보를 반환한다") {
          val mockAuthentication = UsernamePasswordAuthenticationToken(userId, null, null)

          webTestClient.mutateWith(mockAuthentication(mockAuthentication)).get()
            .uri("/api/v1/users/me")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponseDto>()
            .consumeWith {
              val userResponse = it.responseBody!!
              userResponse.email shouldBe user.email
              userResponse.nickname shouldBe user.nickname
              userResponse.isEmployed shouldBe user.isEmployed
              userResponse.profileImageUrl shouldNotBe user.profileImageUrl
              userResponse.gender shouldBe user.gender
              userResponse.introductionComment shouldBe user.introductionComment
              userResponse.phoneNumber shouldBe user.phoneNumber
              userResponse.birthDate shouldBe user.birthDate
            }
        }
      }
    }

    describe("PUT /api/v1/users/me") {
      context("인증된 사용자의 정보를 변경할 때") {
        it("사용자의 변경된 정보를 반환한다") {
          val mockAuthentication = UsernamePasswordAuthenticationToken(userId, null, null)
          val updateUserRequestDto = UpdateUserRequestDto(
            profileImageUrl = "http://new.image.url",
            name = "newName",
            nickname = "newNickname",
            phoneNumber = "010-1234-5678",
            birthDate = LocalDate.of(2000, 1, 1),
            gender = Gender.FEMALE,
            isEmployed = true,
            introductionComment = "newIntroductionComment"
          )

          webTestClient.mutateWith(mockAuthentication(mockAuthentication)).put()
            .uri("/api/v1/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updateUserRequestDto)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponseDto>()
            .consumeWith {
              val userResponse = it.responseBody!!
              userResponse.profileImageUrl shouldNotBe updateUserRequestDto.profileImageUrl
              userResponse.name shouldBe updateUserRequestDto.name
              userResponse.nickname shouldBe updateUserRequestDto.nickname
              userResponse.phoneNumber shouldBe updateUserRequestDto.phoneNumber
              userResponse.birthDate shouldBe updateUserRequestDto.birthDate
              userResponse.gender shouldBe updateUserRequestDto.gender
              userResponse.isEmployed shouldBe updateUserRequestDto.isEmployed
              userResponse.introductionComment shouldBe updateUserRequestDto.introductionComment
            }
        }
      }
    }

    describe("DELETE /api/v1/users/me") {
      context("인증된 사용자의 정보를 삭제(Soft Delete)할 때") {
        it("사용자의 정보를 삭제(Soft Delete)한다") {
          val mockAuthentication = UsernamePasswordAuthenticationToken(userId, null, null)

          webTestClient.mutateWith(mockAuthentication(mockAuthentication)).delete()
            .uri("/api/v1/users/me")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Boolean>()
            .consumeWith {
              val response = it.responseBody!!
              response shouldBe true
            }

          withContext(Dispatchers.IO) {
            val deletedUser = userRepository.findById(userId).awaitFirst()
            deletedUser.isDeleted shouldBe true
          }
        }
      }
    }
  }
}
