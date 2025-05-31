package com.sing4u.kr.session.repository.impl;

import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.repository.SessionCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SessionCustomRepositoryImpl implements SessionCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Session> findAllWithSongsByArtist(Long artistId) {
        String jpql = "SELECT s FROM Session s WHERE s.artist.id = :artistId ORDER BY s.startedAt DESC";
        TypedQuery<Session> query = em.createQuery(jpql, Session.class);
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }
}
