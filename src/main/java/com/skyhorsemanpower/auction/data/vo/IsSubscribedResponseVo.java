package com.skyhorsemanpower.auction.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyhorsemanpower.auction.status.JsonPropertyEnum;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IsSubscribedResponseVo {

    @JsonProperty(value = JsonPropertyEnum.Constant.IS_SUBSCRIBED)
    private boolean isSubscribed;

    public boolean isSubscribed() {
        return this.isSubscribed;
    }
}
