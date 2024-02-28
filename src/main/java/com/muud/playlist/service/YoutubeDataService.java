package com.muud.playlist.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.muud.emotion.entity.Emotion;
import com.muud.playlist.entity.PlayList;
import com.muud.playlist.repository.PlayListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor @Slf4j
public class YoutubeDataService {
    @Value("${youtube.api-key}")
    private String apiKey;
    private final PlayListRepository playListRepository;

    @Transactional
    @Scheduled(cron = "0 30 16 * * ?", zone = "Asia/Seoul")
    public void updateVideoList() throws IOException {
        log.info("playlist data refresh schedule start");
        JsonFactory jsonFactory = new JacksonFactory();
        playListRepository.deleteAll();

        // YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        YouTube youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory,
                request -> {})
                .build();
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id"));

        // API 설정
        search.setKey(apiKey);
        search.setMaxResults(30l);
        search.setOrder("rating");
        search.setType(Collections.singletonList("video"));
        search.setVideoEmbeddable("true");
        search.setVideoDuration("long");
        List<PlayList> playLists = new ArrayList<>();
        for(Emotion emotion: Emotion.values()){
            // 검색어 설정
            search.setQ(emotion.getTitleEmotion()+" PlayList");
            SearchListResponse searchResponse = search.execute();

            List<String> ids= searchResponse.getItems().stream()
                    .map(p -> p.getId().getVideoId())
                    .collect(Collectors.toList());
            List<PlayList> playListList = getVideoDetails(emotion, ids);
            savePlayList(playListList);
        }

    }
    public void savePlayList(List<PlayList> playListList){
        playListRepository.saveAll(playListList);
    }

    public List<PlayList> getVideoDetails(Emotion emotion, List<String> videoIds) throws IOException {
        if(videoIds.isEmpty()) return null;
        JsonFactory jsonFactory = new JacksonFactory();
        YouTube youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory,
                request -> {})
                .build();
        YouTube.Videos.List request = youtube.videos()
                .list(Collections.singletonList("snippet"));
        request.setKey(apiKey)
                .setId(videoIds);
        List<Video> videoList = request.execute().getItems();
        return videoList.stream().map(video -> {
            VideoSnippet snippet = video.getSnippet();
            return PlayList.builder()
                    .videoId(video.getId())
                    .title(snippet.getTitle())
                    .channelName(snippet.getChannelTitle())
                    .tags(snippet.getTags())
                    .emotion(emotion)
                    .build();
        }).collect(Collectors.toList());
    }
}