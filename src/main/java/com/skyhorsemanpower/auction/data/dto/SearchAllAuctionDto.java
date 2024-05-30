package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchAllAuctionDto {
    private String keyword;
    private String category;
    private Integer page;
    private Integer size;
    private String uuid;
    private boolean isUuid;

    @Builder
    public SearchAllAuctionDto(String keyword, String category, Integer page, Integer size, String uuid) {
        this.keyword = keyword;
        this.category = category;
        this.page = page;
        this.size = size;

        // uuid 유무 검증 로직
        if (uuid != null) {
            this.isUuid = true;
            this.uuid = uuid;
        }
    }

    // 서비스 계층에서 사용되는 분기문에 사용
    public boolean isUuid() {
        return this.isUuid;
    }
}
