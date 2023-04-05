package com.wgx.common.to;

import lombok.Data;

import java.util.Date;

/**
 * @author wgx
 * @since 2023/3/29 15:36
 */
@Data
public class SocialUserTo {
    private Integer id;
    private String login;
    private String name;
    private String avatarUrl;
    private String url;
    private String htmlUrl;
    private String remark;
    private String followersUrl;
    private String followingUrl;
    private String gistsUrl;
    private String starredUrl;
    private String subscriptionsUrl;
    private String organizationsUrl;
    private String reposUrl;
    private String eventsUrl;
    private String receivedEventsUrl;
    private String type;
    private Integer publicRepos;
    private Integer publicGists;
    private Integer followers;
    private Integer following;
    private Integer stared;
    private Integer watched;
    private String createdAt;
    private String updatedAt;
    private String accessToken;
    private Date expiration;

}
