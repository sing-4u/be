package com.sing4u.kr.common.utils;

import lombok.experimental.UtilityClass;


import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DataUtils {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> cast(List<?> sourceList, Class<T> requiredType) {
        if (sourceList == null) {
            return null;
        }

        if (requiredType.isEnum()) {
            return sourceList.stream()
                    .map(element -> (T) Enum.valueOf((Class<Enum>) requiredType, element.toString()))
                    .collect(Collectors.toList());
        }

        return sourceList.stream()
                .map(requiredType::cast)
                .collect(Collectors.toList());
    }
}
