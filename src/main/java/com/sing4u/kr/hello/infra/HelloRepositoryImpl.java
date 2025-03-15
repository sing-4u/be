package com.sing4u.kr.hello.infra;

import com.sing4u.kr.hello.domain.Hello;
import com.sing4u.kr.hello.domain.HelloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HelloRepositoryImpl implements HelloRepository {
    private final HelloJpaRepository helloJpaRepository;

    @Override
    public Hello save(Hello hello) {
        return helloJpaRepository.save(hello);
    }

    @Override
    public Optional<Hello> findById(Long id) {
        return helloJpaRepository.findById(id);
    }

    @Override
    public List<Hello> findAll() {
        return helloJpaRepository.findAll();
    }

    @Override
    public Page<Hello> findAllPaged(Pageable pageable) {
        return helloJpaRepository.findAllPaged(pageable);
    }

    @Override
    public Slice<Hello> findAllScrolled(Long lastId, Pageable pageable) {
        return helloJpaRepository.findAllScrolled(lastId, pageable);
    }
}