package com.muud.diary.domain;

import com.muud.bookmark.domain.Bookmark;
import com.muud.diary.domain.dto.DiaryRequest;
import com.muud.emotion.domain.Emotion;
import com.muud.global.common.BaseEntity;
import com.muud.playlist.domain.PlayList;
import com.muud.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Diary extends BaseEntity {

    @Id
    @Column(name = "diary_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300)
    private String content;

    @Column
    private LocalDate referenceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion")
    private Emotion emotion;

    @Column
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private PlayList playList;

    @OneToMany(mappedBy = "diary")
    private List<Bookmark> bookmarkList;

    public Diary(String content, Emotion emotion, User user, LocalDate referenceDate, String imageUrl, PlayList playList) {
        this.id = null;
        this.content = content;
        this.emotion = emotion;
        this.user = user;
        this.referenceDate = referenceDate;
        this.imageUrl = imageUrl;
        this.playList = playList;
    }

    public static Diary of(final User user,
                           final DiaryRequest diaryRequest,
                           final String imageUrl,
                           final PlayList playList) {
        return new Diary(diaryRequest.content(),
                        Emotion.valueOf(diaryRequest.emotionName().toUpperCase()),
                        user,
                        diaryRequest.referenceDate(),
                        imageUrl,
                        playList);
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
