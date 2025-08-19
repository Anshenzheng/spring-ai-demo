package org.an.springai.workflow;


import org.an.springai.pojo.StepStatus;

public abstract class Step {


    private StepStatus status = StepStatus.NEW;

    private boolean ignoreErrorFromPrevStep = false;

    private Step nextStep;

    public Step(boolean ignoreErrorFromPrevStep) {
        this.ignoreErrorFromPrevStep = ignoreErrorFromPrevStep;
    }

    public Step getNextStep() {
        return nextStep;
    }

    public void setNextStep(Step nextStep) {
        this.nextStep = nextStep;
    }

    abstract String stepProcess(String input) throws Exception;

    public String execute(String input) throws Exception {
        status  = StepStatus.RUNNING;
        String result = null;
        try {
            result = stepProcess(input);
            status  = StepStatus.FINISHED;

        } catch (Exception e) {
            status  = StepStatus.FAILED;
            throw e;
        }

        Step nextStep = getNextStep();
        if(result != null && nextStep != null) {
            if(nextStep.ignoreErrorFromPrevStep || !"FAILED".equals(result)) {
                return getNextStep().execute(result);
            }
        }

        return result;
    }

}
