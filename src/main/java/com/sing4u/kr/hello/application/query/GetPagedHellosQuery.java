package com.sing4u.kr.hello.application.query;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.hello.domain.Hello;
import com.sing4u.kr.hello.domain.HelloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetPagedHellosQuery {
    private final HelloRepository helloRepository;


    public PagingResponse<Hello> execute(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hello> pagedResult = helloRepository.findAllPaged(pageable);

        if (pagedResult.isEmpty()) {
            throw new RuntimeException("No Hello records found");
        }

        return PagingResponse.of(pagedResult);
    }
}