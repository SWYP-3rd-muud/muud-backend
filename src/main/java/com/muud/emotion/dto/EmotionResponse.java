package com.muud.emotion.dto;

import com.muud.emotion.entity.Emotion;

public record EmotionResponse(
        String titleEmotion,
        String combinedName,
        String description,
        String emoji,
        String[] tags) {

    public static EmotionResponse from(Emotion emotion) {
        return new EmotionResponse(
                emotion.getTitleEmotion(),
                emotion.getCombinedName(),
                emotion.getDescription(),
                emotion.getEmoji(),
                emotion.getTags());
    }
}