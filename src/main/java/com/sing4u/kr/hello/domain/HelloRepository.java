package com.sing4u.kr.hello.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface HelloRepository {
    Hello save(Hello hello);
    Optional<Hello> findById(Long id);
    List<Hello> findAll();
    Page<Hello> findAllPaged(Pageable pageable);
    Slice<Hello> findAllScrolled(Long lastId, Pageable pageable);
}
