package pps3.plugandproduce.mtex.tags;

public enum AuthorizationPlc {
    AUTH_PLC_EMPTY(-8888),
    AUTH_PLC_CANCELED(-7777),
    AUTH_PLC_RETRY(-4444),
    AUTH_PLC_ORDER_ID(0);

    private final int code;
    private int orderId;

    private AuthorizationPlc(int code) {
        this.code = code;
        this.orderId = -1;
    }

    public static AuthorizationPlc findByCode(int code) {
        for (AuthorizationPlc auth : values()) {
            if (auth.code == code) {
                return auth;
            }
        }

        AuthorizationPlc auth = AUTH_PLC_ORDER_ID;
        auth.orderId = code;

        return auth;
    }

    public static int getCode(AuthorizationPlc auth) {
        if (auth == AUTH_PLC_ORDER_ID) {
            return auth.orderId;
        } else {
            return auth.code;
        }
    }
}
