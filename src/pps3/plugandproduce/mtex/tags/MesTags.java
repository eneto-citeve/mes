package pps3.plugandproduce.mtex.tags;

public class MesTags {
    private static final int MAX_HEARTBEAT = 59;
    private static final int MIN_HEARTBEAT = 0;

    public void setAuthorizationMes(AuthorizationMes authorizationMes) {
        this.authorizationMes = authorizationMes;
    }

    public AuthorizationMes getAuthorizationMes() {
        return authorizationMes;
    }

    private AuthorizationMes authorizationMes;
    private SignalingMes signalingMes;
    private int heartbeat;

    public SignalingMes getSignalingMes() {
        return signalingMes;
    }

    private static final int timeWithoutHeartbeat = 60;
    private String orderDescription;

    public BatchloadMes getBatchloadMes() {
        return batchloadMes;
    }

    public void setBatchloadMes(BatchloadMes batchloadMes) {
        this.batchloadMes = batchloadMes;
    }

    private BatchloadMes batchloadMes;

    public static int getTimeWithoutHeartbeat() {
        return timeWithoutHeartbeat;
    }

    public MesTags() {
        this.authorizationMes = null;
        this.signalingMes = null;
        this.heartbeat = 0;
        this.orderDescription = null;
        this.batchloadMes = null;
    }

    public int incrementHeartbeat() {
        if (this.heartbeat < MAX_HEARTBEAT) {
            this.heartbeat += 1;
        } else {
            this.heartbeat = MIN_HEARTBEAT;
        }

        return this.heartbeat;
    }

    public void resetTags() {
        this.authorizationMes = null;
        this.signalingMes = null;
        this.heartbeat = 0;
        this.orderDescription = null;
        this.batchloadMes = null;
    }
}
