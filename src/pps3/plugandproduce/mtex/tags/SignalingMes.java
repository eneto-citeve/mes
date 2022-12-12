package pps3.plugandproduce.mtex.tags;

public enum SignalingMes {
    SIGNALING_MES_NORMAL_OPERATION(0),
    SIGNALING_MES_ERROR(1),
    SIGNALING_MES_NEXT_ORDER_ERROR(5);

    private final int code;

    private SignalingMes(int code) {
        this.code = code;
    }

    public static SignalingMes findByCode(int code) {
        for (SignalingMes signal : values()) {
            if (signal.code == code) {
                return signal;
            }
        }
        return null;
    }
}
