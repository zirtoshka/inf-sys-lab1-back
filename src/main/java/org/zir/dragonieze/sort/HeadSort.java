package org.zir.dragonieze.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;


@Getter
@RequiredArgsConstructor
public enum HeadSort {
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    EYES_ASC(Sort.by(Sort.Direction.ASC, "eyesCount")),
    EYES_DESC(Sort.by(Sort.Direction.DESC, "eyesCount"));

    private final Sort sortValue;
}