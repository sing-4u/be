// 📁 application/dto/SongRankResponse.java
package com.sing4u.kr.songrequest.application.dto;

public record SongRankResponse(
    String songTitle,
    long count
) {}