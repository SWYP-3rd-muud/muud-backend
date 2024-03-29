package com.muud.global.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T>{

    private List<T> content;
    private int totalCount;

    public PageResponse(List<T> content, int totalCount) {
        this.content = content;
        this.totalCount = totalCount;
    }

    public PageResponse(Page<T> page){
        this.content = page.getContent();
        this.totalCount = page.getNumberOfElements();
    }
}
