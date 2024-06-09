package pmeet.pmeetserver.common.utils.page

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

class SliceResponse {
  companion object {
    fun <T> of(
      content: MutableList<T>,
      pageable: Pageable
    ): Slice<T> {
      val hasNext = content.size > pageable.pageSize
      if (hasNext) {
        content.removeLast()
      }
      return SliceImpl(content, pageable, hasNext)
    }
  }
}
