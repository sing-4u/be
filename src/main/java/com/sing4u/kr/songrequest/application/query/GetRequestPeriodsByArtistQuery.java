package com.sing4u.kr.songrequest.application.query;

import com.sing4u.kr.songrequest.application.query.dao.RequestPeriodDao;
import com.sing4u.kr.songrequest.application.query.dto.RequestPeriodView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GetRequestPeriodsByArtistQuery {

    private final RequestPeriodDao dao;

    public GetRequestPeriodsByArtistQuery(RequestPeriodDao dao) {
        this.dao = dao;
    }

    public List<RequestPeriodView> execute(UUID artistId) {
        return dao.findRequestPeriodViewsByArtistId(artistId);
    }
}