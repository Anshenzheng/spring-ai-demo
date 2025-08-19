package org.an.springai.workflow;

public class FeedKeyAccuisitionStep extends Step{

    private static final String REQUIREMENT_GET_LOGFILE_PATH_PROMPT = """
            你是一个资深的需求分析师，请分析一下业务需求：
            需求描述:{input}
            
            请从一下角度进行分析：
            1. 通过用户输入获得feed name, 并根据feed name查询数据库获得唯一的feed_key; 或者直接从用户出获取feed_key
            2. 如果用提供的feed name中包含了固定的日期或时间戳，则将其替换”%”以用于SQL的模糊查询
            3. Feed key 必须以 "F_" 或 "E_"或 "A_"或 "D_"作为开头，如果用户提供的Feed key 不符合该规范，则要求其重新提供准确的feed key, 
            如果用户无法提供准确的feed key, 则建议其提供 feed name 或者dataset name
            4. 根据feed_key, 从数据库中获得job_name, 并根据job_name 组装获得日志文件名称 {job_name}.err 及 {job_name}.output
            5. 如果从数据库中查询到多个job name, 则让其进行确认哪个才是真正想要找的job
            
            如无法实现， 请直接回复 “抱歉，暂时无法处理，请联系DIET 开发人员。”
            """;

    public FeedKeyAccuisitionStep(boolean ignoreErrorFromPrevStep) {
        super(ignoreErrorFromPrevStep);
    }

    @Override
    String stepProcess(String input) throws Exception {

        return "";
    }
}
