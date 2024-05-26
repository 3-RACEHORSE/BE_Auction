package com.skyhorsemanpower.auction.status;

import lombok.Getter;

@Getter
public enum PageState {
    DEFAULT(0, 5);

    private int page;
    private int size;

    PageState(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}