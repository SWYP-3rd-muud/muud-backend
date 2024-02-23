package com.muud.diary.dto;

import com.muud.diary.domain.Diary;
import com.muud.emotion.entity.Emotion;

import java.time.LocalDateTime;

public record DiaryResponse(Long diaryId,
                            String content,
                            Emotion emotion,
                            LocalDateTime createdDate,
                            LocalDateTime updatedDate) {
    public static DiaryResponse from(Diary diary) {
        return new DiaryResponse(
                diary.getDiaryId(),
                diary.getContent(),
                diary.getEmotion(),
                diary.getCreatedDate(),
                diary.getUpdatedDate());
    }
}
