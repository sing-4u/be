package com.sing4u.kr.hello.application.query;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.hello.domain.Hello;
import com.sing4u.kr.hello.domain.HelloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetScrolledHellosQuery {
    private final HelloRepository helloRepository;

    public PagingResponse<Hello> execute(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Hello> scrolledResult = helloRepository.findAllScrolled(lastId, pageable);

        if (scrolledResult.isEmpty()) {
            throw new RuntimeException("No more Hello records found");
        }

        return PagingResponse.of(scrolledResult);
    }
}