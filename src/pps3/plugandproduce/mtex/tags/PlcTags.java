package pps3.plugandproduce.mtex.tags;

public class PlcTags {
    private MachineState machineState;

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    private OperationMode operationMode;
    private ProductionCycle productionCycle;

    public BatchloadPlc getBatchloadPlc() {
        return batchloadPlc;
    }

    public void setBatchloadPlc(BatchloadPlc batchloadPlc) {
        this.batchloadPlc = batchloadPlc;
    }

    private BatchloadPlc batchloadPlc;

    public AuthorizationPlc getAuthorizationPlc() {
        return authorizationPlc;
    }

    public void setAuthorizationPlc(AuthorizationPlc authorizationPlc) {
        this.authorizationPlc = authorizationPlc;
    }

    private AuthorizationPlc authorizationPlc;
    //public static boolean stepActive;
    private int checksumPlcMes;
    private int incrementBatchPlc;
    private int incrementStepPlc;

    public PlcTags() {
        this.machineState = null;
        this.operationMode = null;
        this.productionCycle = null;
        this.batchloadPlc = null;
        this.authorizationPlc = null;
        this.checksumPlcMes = -1;
        this.incrementBatchPlc = -1;
        this.incrementStepPlc = -1;
    }
}
