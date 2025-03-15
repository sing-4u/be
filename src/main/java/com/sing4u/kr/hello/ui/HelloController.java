package com.sing4u.kr.hello.ui;

import com.sing4u.kr.common.response.PagingResponse;
import com.sing4u.kr.hello.application.command.CreateHelloUseCase;
import com.sing4u.kr.hello.application.query.GetAllHellosQuery;
import com.sing4u.kr.hello.application.query.GetPagedHellosQuery;
import com.sing4u.kr.hello.application.query.GetScrolledHellosQuery;
import com.sing4u.kr.hello.domain.Hello;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {
    private final CreateHelloUseCase createHelloUseCase;
    private final GetAllHellosQuery getAllHellosQuery;

    private final GetPagedHellosQuery getPagedHellosQuery;
    private final GetScrolledHellosQuery getScrolledHellosQuery;
    @PostMapping
    public ResponseEntity<Hello> createHello(@RequestParam String message) {
        return ResponseEntity.ok(createHelloUseCase.execute(message));
    }

    @GetMapping
    public ResponseEntity<List<Hello>> getAllHellos() {
        return ResponseEntity.ok(getAllHellosQuery.execute());
    }

    @GetMapping("/paged")
    public ResponseEntity<PagingResponse<Hello>> getPagedHellos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(getPagedHellosQuery.execute(page, size));
    }

    @GetMapping("/scrolled")
    public ResponseEntity<PagingResponse<Hello>> getScrolledHellos(
            @RequestParam Long lastId,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(getScrolledHellosQuery.execute(lastId, size));
    }
}