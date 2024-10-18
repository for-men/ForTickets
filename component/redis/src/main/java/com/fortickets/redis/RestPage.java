package com.fortickets.redis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * Page<T> 데이터를 캐싱하기 위한 객체. Page<T>를 리턴하는 부분을 감싸서 사용한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"}) // JSON 직렬화 시 무시할 필드를 지정
public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) // JSON에서 이 클래스의 인스턴스를 생성할 때 사용할 생성자를 지정
    public RestPage(@JsonProperty("content") List<T> content,
        @JsonProperty("number") int page,
        @JsonProperty("size") int size,
        @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page, size), total);
    }

    public RestPage(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }
}
