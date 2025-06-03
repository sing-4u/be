package com.sing4u.kr.music;

import com.sing4u.kr.music.dto.TrackDto;

public interface MusicInterface {
    /**
     * 현재 서비스 구현체가 어떤 음악 플랫폼을 위한 것인지 식별자를 반환.
     * 이 값은 나중에 특정 플랫폼 서비스를 선택하는 데 사용.
     * (예: "SPOTIFY", "YOUTUBE_MUSIC")
     *
     * @return 플랫폼 식별자 문자열 (대문자 권장)
     */
    String getPlatformIdentifier();

    /**
     * 주어진 플랫폼별 트랙 ID를 사용하여 해당 음악 플랫폼에서 트랙의 상세 정보를 조회.
     *
     * @param platformTrackId 조회할 음악 플랫폼의 고유 트랙 ID
     * @return 트랙 상세 정보를 담은 {@link TrackDto}. 정보를 찾지 못하거나 오류 발생 시 null을 반환.
     */
    TrackDto getTrackDetails(String platformTrackId);
}
