package com.muud.bookmark.controller;

import com.muud.auth.jwt.Auth;
import com.muud.bookmark.service.BookmarkService;
import com.muud.bookmark.domain.Bookmark;
import com.muud.bookmark.domain.dto.BookmarkResponse;
import com.muud.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Auth
    @GetMapping("/diaries/bookmarks")
    public ResponseEntity<List<BookmarkResponse>> getBookmarkList(@RequestAttribute("user") final User user) {
        return ResponseEntity.ok(bookmarkService.getBookmarkList(user.getId()));
    }

    @Auth
    @GetMapping("/diaries/{diaryId}/bookmarks")
    public ResponseEntity<Boolean> checkBookmark(@RequestAttribute("user") final User user,
                                                 @PathVariable("diaryId") final Long diaryId) {
        boolean isBookmarked = bookmarkService.isBookmarked(user.getId(), diaryId);
        return ResponseEntity.ok(isBookmarked);
    }

    @Auth
    @PostMapping("/diaries/{diaryId}/bookmarks")
    public ResponseEntity<Object> addBookmark(@RequestAttribute("user") final User user,
                                              @PathVariable("diaryId") final Long diaryId) {
        Bookmark bookmark = bookmarkService.addBookmark(user.getId(), diaryId);
        return ResponseEntity.created(URI.create("/bookmarks/"+bookmark.getId())).build();
    }

    @Auth
    @DeleteMapping("/diaries/{diaryId}/bookmarks")
    public ResponseEntity<Object> removeBookmark(@RequestAttribute("user") final User user,
                                                 @PathVariable("diaryId") final Long diaryId) {
        bookmarkService.removeBookmark(user.getId(), diaryId);
        return ResponseEntity.noContent().build();
    }
}