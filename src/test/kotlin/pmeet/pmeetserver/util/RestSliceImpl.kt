package pmeet.pmeetserver.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl

@JsonIgnoreProperties(ignoreUnknown = true)
class RestSliceImpl<T> @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
constructor(
  @JsonProperty("content") content: List<T>,
  @JsonProperty("number") number: Int,
  @JsonProperty("size") size: Int,
  @JsonProperty("pageable") pageable: JsonNode,
  @JsonProperty("last") last: Boolean, // last는 SliceImpl의 hasNext의 반대로 생성된다.
  @JsonProperty("first") first: Boolean,
  @JsonProperty("sort") sort: JsonNode,
  @JsonProperty("numberOfElements") numberOfElements: Int,
  @JsonProperty("empty") empty: Boolean
) : SliceImpl<T>(content, PageRequest.of(number, size), !last)
