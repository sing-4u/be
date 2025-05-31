package com.sing4u.kr.user.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sing4u.kr.user.entity.QUser;
import com.sing4u.kr.user.entity.User;
import com.sing4u.kr.user.entity.enums.UserType;
import com.sing4u.kr.user.repository.UserCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<User> findArtistsWithKeywordAndRandomOrder(String keyword, Pageable pageable) {
        QUser user = QUser.user;

        BooleanBuilder condition = new BooleanBuilder()
                .and(user.userType.eq(UserType.ARTIST));

        if (keyword != null && !keyword.isBlank()) {
            condition.and(user.nickname.containsIgnoreCase(keyword));
        }

        NumberTemplate<Double> rand = Expressions.numberTemplate(Double.class, "RAND()");

        List<User> result = queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(user.isOpen.desc(), rand.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = result.size() > pageable.getPageSize();

        if (hasNext) {
            result.remove(result.size() - 1);
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }
}

