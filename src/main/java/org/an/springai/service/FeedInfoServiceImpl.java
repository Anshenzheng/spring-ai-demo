package org.an.springai.service;

import org.an.springai.pojo.FeedInfo;
import org.an.springai.repository.FeedInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedInfoServiceImpl implements FeedInfoService {

    @Autowired
    private FeedInfoRepository feedInfoRepository;

    @Override
    public List<FeedInfo> getFeedInfo(String query) {
        return feedInfoRepository.getFeedInfo(query);
    }

    @Override
    public List<FeedInfo> getFeedInfoByAI(String aiQuery) {
        return List.of();
    }
}
