package org.an.springai.tools;

import org.an.springai.service.SafeSqlExecutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public  class FeedInfoQueryTools {

    final SafeSqlExecutor safeSqlExecutor;

    public FeedInfoQueryTools(SafeSqlExecutor safeSqlExecutor) {
        this.safeSqlExecutor = safeSqlExecutor;
    }

    @Tool(description = "获取feed相关信息，入参是SQL查询语句，出参是查询出的feed信息")
    List<Map<String, Object>> getFeedInfo(String query){
        System.out.println(query);
        return safeSqlExecutor.executeSafeQuery(query);
    }


}
