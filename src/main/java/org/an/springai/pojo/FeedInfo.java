package org.an.springai.pojo;

public class FeedInfo {
    private String feed_key;
    private String feed_name;
    private String feed_type;
    private String workstream;
    private String source_app_name;
    private String destination_app_name;
    private String job_name;
    private String source_contact;
    private String destination_contact;
    private String status;

    public FeedInfo() {
    }

    public FeedInfo(String feed_key, String feed_name, String feed_type, String workstream, String source_app_name,
                    String destination_app_name, String job_name, String source_contact, String destination_contact, String status) {
        this.feed_key = feed_key;
        this.feed_name = feed_name;
        this.feed_type = feed_type;
        this.workstream = workstream;
        this.source_app_name = source_app_name;
        this.destination_app_name = destination_app_name;
        this.job_name = job_name;
        this.source_contact = source_contact;
        this.destination_contact = destination_contact;
        this.status = status;
    }

    public String getFeed_key() {
        return feed_key;
    }

    public void setFeed_key(String feed_key) {
        this.feed_key = feed_key;
    }

    public String getFeed_name() {
        return feed_name;
    }

    public void setFeed_name(String feed_name) {
        this.feed_name = feed_name;
    }

    public String getFeed_type() {
        return feed_type;
    }

    public void setFeed_type(String feed_type) {
        this.feed_type = feed_type;
    }

    public String getWorkstream() {
        return workstream;
    }

    public void setWorkstream(String workstream) {
        this.workstream = workstream;
    }

    public String getSource_app_name() {
        return source_app_name;
    }

    public void setSource_app_name(String source_app_name) {
        this.source_app_name = source_app_name;
    }

    public String getDestination_app_name() {
        return destination_app_name;
    }

    public void setDestination_app_name(String destination_app_name) {
        this.destination_app_name = destination_app_name;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getSource_contact() {
        return source_contact;
    }

    public void setSource_contact(String source_contact) {
        this.source_contact = source_contact;
    }

    public String getDestination_contact() {
        return destination_contact;
    }

    public void setDestination_contact(String destination_contact) {
        this.destination_contact = destination_contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
