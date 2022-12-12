package pps3.plugandproduce.mtex.tags;

public enum AuthorizationMes {
    AUTH_MES_EMPTY(-8888),
    AUTH_MES_NO_AUTHORIZATION(-3333),
    AUTH_MES_ORDER_ID(0);

    private final int code;
    private int orderId;

    private AuthorizationMes(int code) {
        this.code = code;
        this.orderId = -1;
    }

    public static AuthorizationMes findByCode(int code) {
        for (AuthorizationMes auth : values()) {
            if (auth.code == code) {
                return auth;
            }
        }

        AuthorizationMes auth = AUTH_MES_ORDER_ID;
        auth.orderId = code;

        return auth;
    }

    public static int getCode(AuthorizationMes auth) {
        if (auth == AUTH_MES_ORDER_ID) {
            return auth.orderId;
        } else {
            return auth.code;
        }
    }
}
