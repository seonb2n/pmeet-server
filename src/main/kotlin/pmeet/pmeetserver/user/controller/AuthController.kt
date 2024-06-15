package pmeet.pmeetserver.user.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import pmeet.pmeetserver.user.service.oauth.OauthFacadeService
import pmeet.pmeetserver.user.dto.request.CheckMailRequestDto
import pmeet.pmeetserver.user.dto.request.CheckNickNameRequestDto
import pmeet.pmeetserver.user.dto.request.SendVerificationCodeRequestDto
import pmeet.pmeetserver.user.dto.request.SetPasswordRequestDto
import pmeet.pmeetserver.user.dto.request.SignInRequestDto
import pmeet.pmeetserver.user.dto.request.SignUpRequestDto
import pmeet.pmeetserver.user.dto.request.VerifyVerificationCodeRequestDto
import pmeet.pmeetserver.user.dto.response.UserJwtDto
import pmeet.pmeetserver.user.dto.response.UserSignUpResponseDto
import pmeet.pmeetserver.user.service.UserFacadeService
import java.net.URI

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
  private val userFacadeService: UserFacadeService,
  private val oauthFacadeService: OauthFacadeService
) {
  // 주석 추가
  @PostMapping("/sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  suspend fun createUser(@RequestBody @Valid requestDto: SignUpRequestDto): UserSignUpResponseDto {
    return userFacadeService.save(requestDto)
  }

  @PostMapping("/sign-in")
  @ResponseStatus(HttpStatus.OK)
  suspend fun signIn(@RequestBody @Valid requestDto: SignInRequestDto): UserJwtDto {
    return userFacadeService.signIn(requestDto)
  }

  @GetMapping("/sign-in/google")
  @ResponseStatus(HttpStatus.OK)
  suspend fun signInGoogle(@RequestParam code: String, exchange: ServerWebExchange) {
    val userJwt = oauthFacadeService.loginGoogleOauth(code)
    val redirectUrl = buildString {
      append("https://pmeet.site/?")
      append("userId=${userJwt.userId}&")
      append("accessToken=${userJwt.accessToken}&")
      append("refreshToken=${userJwt.refreshToken}")
    }
    exchange.response.statusCode = HttpStatus.SEE_OTHER
    exchange.response.headers.location = URI.create(redirectUrl)
  }

  @GetMapping("/sign-in/naver")
  suspend fun signInNaver(@RequestParam code: String, @RequestParam state: String, exchange: ServerWebExchange) {
    val userJwt = oauthFacadeService.loginNaverOauth(code, state)
    val redirectUrl = buildString {
      append("https://pmeet.site/?")
      append("userId=${userJwt.userId}&")
      append("accessToken=${userJwt.accessToken}&")
      append("refreshToken=${userJwt.refreshToken}")
    }
    exchange.response.statusCode = HttpStatus.SEE_OTHER
    exchange.response.headers.location = URI.create(redirectUrl)
  }

  @GetMapping("/sign-in/kakao")
  @ResponseStatus(HttpStatus.OK)
  suspend fun signInKakao(@RequestParam code: String, exchange: ServerWebExchange) {
    val userJwt = oauthFacadeService.loginKakaoOauth(code)
    val redirectUrl = buildString {
      append("https://pmeet.site/?")
      append("userId=${userJwt.userId}&")
      append("accessToken=${userJwt.accessToken}&")
      append("refreshToken=${userJwt.refreshToken}")
    }
    exchange.response.statusCode = HttpStatus.SEE_OTHER
    exchange.response.headers.location = URI.create(redirectUrl)
  }

  @PostMapping("/nickname/duplicate")
  @ResponseStatus(HttpStatus.OK)
  suspend fun isDuplicateNickName(@RequestBody @Valid requestDto: CheckNickNameRequestDto): Boolean {
    return userFacadeService.isDuplicateNickName(requestDto)
  }

  @PostMapping("/mail/duplicate")
  @ResponseStatus(HttpStatus.OK)
  suspend fun isDuplicateMail(@RequestBody @Valid requestDto: CheckMailRequestDto): Boolean {
    return userFacadeService.isDuplicateMail(requestDto)
  }

  @PostMapping("/verification-code")
  @ResponseStatus(HttpStatus.OK)
  suspend fun sendVerificationCode(@RequestBody @Valid requestDto: SendVerificationCodeRequestDto): Boolean {
    return userFacadeService.sendVerificationCode(requestDto)
  }

  @PostMapping("/verification-code/verify")
  @ResponseStatus(HttpStatus.OK)
  suspend fun verifyVerificationCode(@RequestBody @Valid requestDto: VerifyVerificationCodeRequestDto): Boolean {
    return userFacadeService.verifyVerificationCode(requestDto)
  }

  @PutMapping("/password")
  suspend fun setPassword(@RequestBody @Valid requestDto: SetPasswordRequestDto): Boolean {
    return userFacadeService.setPassword(requestDto)
  }
}
