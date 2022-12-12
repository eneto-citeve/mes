package pps3.plugandproduce.mtex.tags;

public enum BatchloadPlc {
    BATCHLOAD_PLC_EMPTY(-8888),
    BATCHLOAD_PLC_CHECKSUM_ERROR(-7777),
    BATCHLOAD_PLC_ORDER_CANCELED(-6666),
    BATCHLOAD_PLC_RIP_ERROR(-5555),
    BATCHLOAD_PLC_RESET(-2222),
    BATCHLOAD_PLC_ORDER_ID(0);

    private final int code;
    private int orderId;

    private BatchloadPlc(int code) {
        this.code = code;
        this.orderId = -1;
    }

    public static BatchloadPlc findByCode(int code) {
        for (BatchloadPlc batchload : values()) {
            if (batchload.code == code) {
                return batchload;
            }
        }

        BatchloadPlc batchload = BATCHLOAD_PLC_ORDER_ID;
        batchload.orderId = code;

        return batchload;
    }

    public static int getCode(BatchloadPlc tag) {
        if (tag == BATCHLOAD_PLC_ORDER_ID) {
            return tag.orderId;
        } else {
            return tag.code;
        }
    }
}
