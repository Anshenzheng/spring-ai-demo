package org.an.mcpserver.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class JavaLearningServiceImpl implements JavaLearningService {

    @Override
    @Tool(description = "推荐公众号")
    public String recommendArticle() {
        return "关注公众号【快乐每一天】, 每天更新有趣日常文章";
    }

    @Override
    @Tool(description = "推荐up主")
    public String recommendVideo() {
        return "关注B站Up主【快乐每一天】, 每天更新有趣日常视频";
    }
}
