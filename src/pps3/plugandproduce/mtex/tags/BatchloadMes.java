package pps3.plugandproduce.mtex.tags;

public enum BatchloadMes {
    BATCHLOAD_MES_EMPTY(-8888),
    BATCHLOAD_MES_ORDER_ID(0);

    private final int code;
    private int orderId;

    private BatchloadMes(int code) {
        this.code = code;
        this.orderId = -1;
    }

    public static BatchloadMes findByCode(int code) {
        for (BatchloadMes batchload : values()) {
            if (batchload.code == code) {
                return batchload;
            }
        }

        BatchloadMes batchload = BATCHLOAD_MES_ORDER_ID;
        batchload.orderId = code;

        return batchload;
    }

    public static int getCode(BatchloadMes tag) {
        if (tag == BATCHLOAD_MES_EMPTY) {
            return tag.orderId;
        } else {
            return tag.code;
        }
    }
}
