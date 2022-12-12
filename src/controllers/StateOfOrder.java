package controllers;

public enum StateOfOrder {
    REQUESTED_ACCEPTED(1),
    REQUESTED_DENIED(2),
    CREATED_SETUP(3),
    CREATED_RUNNING(4),
    FINISHED_SUCCESSFULLY(5),
    FINISHED_CANCELED(6);

    private final int code;

    private StateOfOrder(int code) {
        this.code = code;
    }

    public static int getCode(StateOfOrder state) {
        return state.code;
    }
}
