package pps3.plugandproduce.mtex.tags;

public enum OperationMode {
    ONLINE(false),
    OFFLINE(true);

    private final boolean code;

    private OperationMode(boolean code) {
        this.code = code;
    }

    public static OperationMode findByCode(boolean code) {
        for(OperationMode mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        return null;
    }

    public static boolean getCode(OperationMode tag) {
        return tag.code;
    }
}
