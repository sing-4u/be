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

import java.util.List;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findArtistsWithKeywordAndRandomOrder(String keyword, long seed, int offset, int limit) {
        QUser user = QUser.user;

        BooleanBuilder condition = new BooleanBuilder()
                .and(user.userType.eq(UserType.ARTIST));

        if (keyword != null && !keyword.isBlank()) {
            condition.and(user.nickname.containsIgnoreCase(keyword));
        }

        NumberTemplate<Double> rand = Expressions.numberTemplate(Double.class, "RAND({0})", seed);

        return queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(
                        user.isOpen.desc(),
                        rand.asc()
                )
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}
