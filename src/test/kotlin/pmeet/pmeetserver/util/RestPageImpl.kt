package pmeet.pmeetserver.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@JsonIgnoreProperties(ignoreUnknown = true)
class RestPageImpl<T> @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
constructor(
  @JsonProperty("content") content: List<T>,
  @JsonProperty("number") number: Int,
  @JsonProperty("size") size: Int,
  @JsonProperty("totalElements") totalElements: Long,
  @JsonProperty("pageable") pageable: JsonNode,
  @JsonProperty("first") first: Boolean,
  @JsonProperty("last") last: Boolean,
  @JsonProperty("totalPages") totalPages: Int,
  @JsonProperty("sort") sort: JsonNode,
  @JsonProperty("numberOfElements") numberOfElements: Int
) : PageImpl<T>(content, PageRequest.of(number, size), totalElements)
