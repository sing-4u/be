package com.sing4u.kr.hello.infra;

import com.sing4u.kr.hello.domain.Hello;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloJpaRepository extends JpaRepository<Hello, Long> {

    @Query(value = "SELECT h FROM Hello h",
            countQuery = "SELECT COUNT(h) FROM Hello h")
    Page<Hello> findAllPaged(Pageable pageable);

    @Query("SELECT h FROM Hello h WHERE h.helloNo > :lastId ORDER BY h.helloNo ASC")
    Slice<Hello> findAllScrolled(Long lastId, Pageable pageable);
}

