package org.an.springai.service;

import org.an.springai.pojo.FeedInfo;

import java.util.List;

public interface FeedInfoService {
    List<FeedInfo> getFeedInfo(String query);

    List<FeedInfo> getFeedInfoByAI(String aiQuery);
}
