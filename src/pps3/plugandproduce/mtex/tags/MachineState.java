package pps3.plugandproduce.mtex.tags;

public enum MachineState {
    MACHINE_RUNNING(0),
    MACHINE_STOPPED(1),
    MACHINE_PRINTING_HEAD_MALFUNCTION(200),
    MACHINE_PRINTING_HEAD_TEMPERATURE_OUTSIDE_RANGE(201),
    MACHINE_CARRYING_SYSTEM_MALFUNCTION(300),
    MACHINE_CARRYING_SYSTEM_TRANSPORT_ERROR(301),
    MACHINE_CARRYING_SYSTEM_MOVEMENT_ERROR(302),
    MACHINE_CARRYING_SYSTEM_TEMPERATURE_OUTSIDE_RANGE(303),
    MACHINE_CARRYING_SYSTEM_HUMIDITY_OUTSIDE_RANGE(304),
    MACHINE_PLC_ERROR(400),
    MACHINE_EMERGENCY_STOP(500),
    MACHINE_INK_SYSTEM_ERROR(600),
    MACHINE_MAINTENANCE_MODE(901),
    MACHINE_SETUP_MODE(902);

    private final int code;

    private MachineState(int code) {
        this.code = code;
    }

    public static MachineState findByCode(int code) {
        for (MachineState state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        return null;
    }

    public static int getCode(MachineState tag) {
        return tag.code;
    }
}
