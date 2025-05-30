package com.sing4u.kr.session.repository;

import com.sing4u.kr.session.entity.Session;
import com.sing4u.kr.session.enums.SessionStatus;
import com.sing4u.kr.session.repository.impl.SessionCustomRepositoryImpl;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionRepositoryCustomImplTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Session> typedQuery;

    @InjectMocks
    private SessionCustomRepositoryImpl sessionCustomRepositoryImpl;

    private User artist;
    private Session session;

    // Helper method to create a User
    private User createArtist(Long id, String nickName) {
        return User.testUserBuilder(id, nickName, UserType.ARTIST);
    }

    // Helper method to create a Session
    private Session createSession(Long id, User artist, SessionStatus status, List<com.sing4u.kr.customSongRequest.entity.SongRequest> songRequests) {
        return Session.builder()
                .id(id)
                .artist(artist)
                .status(status)
                .startedAt(LocalDateTime.now().minusHours(1)) // 일관된 시간 설정
                .closedAt(status == SessionStatus.CLOSE ? LocalDateTime.now() : null)
                .songRequests(songRequests == null ? new ArrayList<>() : songRequests)
                .build();
    }

    @BeforeEach
    void setUp() {
        artist = createArtist(1L, "Test Artist");
        session = createSession(100L, artist, SessionStatus.OPEN, new ArrayList<>());
    }

    @Test
    @DisplayName("findAllWithSongsByArtist - 아티스트 ID로 세션과 신청곡 목록 조회 성공")
    void findAllWithSongsByArtist_whenArtistHasSessions_returnsSessionsWithSongs() {
        // given
        Long artistId = artist.getId();
        List<Session> expectedSessions = Collections.singletonList(session);
        String expectedJpql = "SELECT s FROM Session s LEFT JOIN FETCH s.songRequests sr WHERE s.artist.id = :artistId ORDER BY s.startedAt DESC";

        when(em.createQuery(eq(expectedJpql), eq(Session.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("artistId"), eq(artistId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedSessions);

        // when
        List<Session> actualSessions = sessionCustomRepositoryImpl.findAllWithSongsByArtist(artistId);

        // then
        assertThat(actualSessions).isEqualTo(expectedSessions);
        verify(em).createQuery(eq(expectedJpql), eq(Session.class));
        verify(typedQuery).setParameter(eq("artistId"), eq(artistId));
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findAllWithSongsByArtist - 해당 아티스트의 세션이 없을 때 빈 목록 반환")
    void findAllWithSongsByArtist_whenArtistHasNoSessions_returnsEmptyList() {
        // given
        Long artistId = artist.getId();
        String expectedJpql = "SELECT DISTINCT s FROM Session s LEFT JOIN FETCH s.songRequests sr WHERE s.artist.id = :artistId ORDER BY s.startedAt DESC";

        when(em.createQuery(eq(expectedJpql), eq(Session.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("artistId"), eq(artistId))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // when
        List<Session> actualSessions = sessionCustomRepositoryImpl.findAllWithSongsByArtist(artistId);

        // then
        assertThat(actualSessions).isNotNull().isEmpty();
        verify(em).createQuery(eq(expectedJpql), eq(Session.class));
        verify(typedQuery).setParameter(eq("artistId"), eq(artistId));
        verify(typedQuery).getResultList();
    }
}