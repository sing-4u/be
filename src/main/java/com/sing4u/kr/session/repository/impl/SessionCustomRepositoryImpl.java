package com.sing4u.kr.session.repository.impl;

import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.repository.SessionCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public class SessionCustomRepositoryImpl implements SessionCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override // SessionRepositoryCustom 인터페이스의 추상 메소드를 구현
    public List<Session> findAllWithSongsByArtist(Long artistId) {

        // 여기에 실제 데이터베이스 조회 로직
        String jpql =
                "SELECT s FROM Session s LEFT JOIN FETCH s.songRequests sr WHERE s.artist.id = :artistId ORDER BY s.startedAt DESC";
        TypedQuery<Session> query = em.createQuery(jpql, Session.class);
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }
}
