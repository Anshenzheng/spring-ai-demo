package org.an.springai.repository;

import org.an.springai.pojo.FeedInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FeedInfoRepository {
    private final JdbcTemplate jdbcTemplate;
    public FeedInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper实现
    private static final RowMapper<FeedInfo> FEED_ROW_MAPPER = (rs, rowNum) -> {
        FeedInfo feedInfo = new FeedInfo();
        feedInfo.setFeed_key(rs.getString("feed_key"));
        feedInfo.setFeed_name(rs.getString("feed_name"));
        feedInfo.setFeed_type(rs.getString("feed_type"));
        feedInfo.setDestination_app_name(rs.getString("destination_app_name"));
        feedInfo.setStatus(rs.getString("status"));
        feedInfo.setJob_name(rs.getString("job_name"));
        feedInfo.setWorkstream(rs.getString("workstream"));
        feedInfo.setSource_app_name(rs.getString("source_app_name"));
        feedInfo.setSource_contact(rs.getString("source_contact"));
        feedInfo.setDestination_contact(rs.getString("destination_contact"));
        return feedInfo;
    };

    public List<FeedInfo> getFeedInfo(String query){
        String sql = "SELECT * FROM tbl_feed_basic_info WHERE feed_name LIKE '%"+query+"%' " +
                "OR feed_key LIKE '%"+query+"%' " +
                "OR job_name LIKE '%" +query+"%' " +
                "OR workstream LIKE "+"'%"+query+"%' " +
                "OR source_app_name LIKE '%" +query + "%'";

        return jdbcTemplate.query(sql,FEED_ROW_MAPPER);
    }
}

