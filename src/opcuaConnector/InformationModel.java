package opcuaConnector;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class InformationModel {
    public final Node AUTORIZACAO_MES;
    public final Node SINALIZACAO_MES;
    public final Node HEARTBEAT;
    public final Node TEMPO_SEM_HEARTBEAT;
    public final Node OF_OM_DESC;
    public final Node BATCHLOAD_MES;
    public final Node CHECKSUM_MES_PLC;
    public final Node LER_HISTORICO;
    public final Node ESTADO_EQUIPAMENTO;
    public final Node OFFLINE;
    public final Node CICLO_PRODUTIVO;
    public final Node AUTORIZACAO_PLC;
    public final Node BATCHLOAD_PLC;
    public final Node CHECKSUM_PLC_MES;
    public final Node CONTAGEM;
    public final Node CONSUMO;
    public final Node CONSUMO_TEORICO;

    public InformationModel(int namespace) {
        this.AUTORIZACAO_MES = new Node(namespace, "AUTORIZACAO_MES", Type.D_INTEGER);
        this.SINALIZACAO_MES = new Node(namespace, "SINALIZACAO_MES", Type.U_INTEGER);
        this.HEARTBEAT = new Node(namespace, "HEARTBEAT", Type.U_INTEGER);
        this.TEMPO_SEM_HEARTBEAT = new Node(namespace, "TEMPO_SEM_HEARTBEAT", Type.U_INTEGER);
        this.OF_OM_DESC = new Node(namespace, "OF_OM_DESC", Type.STRING);
        this.BATCHLOAD_MES = new Node(namespace, "BATCHLOAD_MES", Type.D_INTEGER);
        this.CHECKSUM_MES_PLC = new Node(namespace, "CHECKSUM_MES_PLC", Type.U_INTEGER);
        this.LER_HISTORICO = new Node(namespace, "LER_HISTORICO", Type.BOOL);
        this.ESTADO_EQUIPAMENTO = new Node(namespace, "ESTADO_EQUIPAMENTO", Type.U_INTEGER);
        this.OFFLINE = new Node(namespace, "OFFLINE", Type.BOOL);
        this.CICLO_PRODUTIVO = new Node(namespace, "CICLO_PRODUTIVO", Type.BOOL);
        this.AUTORIZACAO_PLC = new Node(namespace, "AUTORIZACAO_PLC", Type.D_INTEGER);
        this.BATCHLOAD_PLC = new Node(namespace, "BATCHLOAD_PLC", Type.D_INTEGER);
        this.CHECKSUM_PLC_MES = new Node(namespace, "CHECKSUM_PLC_MES", Type.U_INTEGER);
        this.CONTAGEM = new Node(namespace, "CONTAGEM", Type.U_D_INTEGER);
        this.CONSUMO = new Node(namespace, "CONSUMO", Type.U_D_INTEGER);
        this.CONSUMO_TEORICO = new Node(namespace, "CONSUMO_TEORICO", Type.U_D_INTEGER);
    }
}