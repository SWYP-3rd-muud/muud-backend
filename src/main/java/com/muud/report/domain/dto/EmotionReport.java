package com.muud.report.domain.dto;

import java.util.List;

public record EmotionReport(int resultCount,
                            List<RankDto<String>> emotionCountList) {
}