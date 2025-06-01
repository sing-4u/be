package com.sing4u.kr.music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MusicPlatformSelector {

    private static final Logger logger = LoggerFactory.getLogger(MusicPlatformSelector.class); // 로깅용
    private final Map<String, MusicInterface> servicesByPlatformName;

    public MusicPlatformSelector(List<MusicInterface> musicPlatformServices) {
        this.servicesByPlatformName = musicPlatformServices.stream()
                .collect(Collectors.toMap(
                        service -> service.getPlatformIdentifier().toUpperCase(),
                        Function.identity(),
                        (existing, replacement) -> {
                            logger.warn("중복된 플랫폼 식별자: {}, 기존 구현체를 유지합니다.", existing.getPlatformIdentifier());
                            return existing;
                        }
                ));
    }

    public Optional<MusicInterface> selectService(String platformIdentifier) {
        if (platformIdentifier == null || platformIdentifier.isBlank()) return Optional.empty();
        return Optional.ofNullable(servicesByPlatformName.get(platformIdentifier.toUpperCase()));
    }
}