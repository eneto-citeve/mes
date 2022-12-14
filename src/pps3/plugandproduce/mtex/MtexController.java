package pps3.plugandproduce.mtex;

import pps3.plugandproduce.mtex.MtexDriver;
import pps3.plugandproduce.mtex.orders.ConsumptionChannel;
import pps3.plugandproduce.mtex.tags.*;

import java.util.concurrent.ExecutionException;

public class MtexController extends Thread {
    private PlcTags plcTags;
    private final MtexDriver mtexDriver;

    public MtexController(MtexDriver mtexDriver) {
        this.plcTags = new PlcTags();
        this.mtexDriver = mtexDriver;

        // TODO: Ler todas as tags

        try {
            processOperationMode();
        } catch (ExecutionException | InterruptedException | NullPointerException exExc) {
            System.out.println("Error while reading tag OFFLINE: "+ exExc);
        }
    }

    // Vai procurar pelos triggers (BATCHLOAD_PLC e AUTORIZACAO_PLC)
    @Override
    public void run() {
        try {
            synchronized (mtexDriver) {
                try {
                    processBatchloadPlc();
                } catch (ExecutionException | InterruptedException | NullPointerException exExc) {
                    System.out.println("Error while reading tag BATCHLOAD_PLC: "+ exExc);
                }

                // Tenho que ler o Auth
                try {
                    processAuthorizationPlc();
                } catch (ExecutionException | InterruptedException | NullPointerException exExc) {
                    System.out.println("Error while reading tag AUTORIZACAO_PLC: "+ exExc);
                }
            }
        } catch (Throwable t) {
            System.out.println("MES from MtexController: " + t.getMessage());
        }

    }

    private void processBatchloadPlc() throws ExecutionException, InterruptedException {
        int code = Integer.valueOf((short) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().BATCHLOAD_PLC));
        System.out.println(code);
        BatchloadPlc batchloadRead = BatchloadPlc.findByCode(code);

        System.out.println(batchloadRead);

        if (plcTags.getBatchloadPlc() != batchloadRead) {
            plcTags.setBatchloadPlc(batchloadRead);
            handleBatchloadPlcChange(batchloadRead);
        }
    }

    private void handleBatchloadPlcChange(BatchloadPlc batchloadRead) {
        switch (batchloadRead) {
            case BATCHLOAD_PLC_EMPTY:
                this.mtexDriver.mtexExecuteOrder();
                break;
            case BATCHLOAD_PLC_CHECKSUM_ERROR:
                this.mtexDriver.mtexResetWorkOrder("CHECKSUM_ERROR");
                break;
            case BATCHLOAD_PLC_ORDER_CANCELED:
                this.mtexDriver.mtexResetWorkOrder("CANCELED");
                break;
            case BATCHLOAD_PLC_RIP_ERROR:
                this.mtexDriver.mtexResetWorkOrder("RIP_FILE_ERROR");
                break;
            case BATCHLOAD_PLC_RESET:
                this.mtexDriver.mtexResetWorkOrder("PLC_ERROR");
                break;
            case BATCHLOAD_PLC_ORDER_ID:
                this.mtexDriver.mtexCheckIfSameOrderId(BatchloadPlc.getCode(plcTags.getBatchloadPlc()));
                break;
        }
    }

    private void processAuthorizationPlc() throws ExecutionException, InterruptedException {
        int code = Integer.valueOf((short) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().AUTORIZACAO_PLC));

        AuthorizationPlc authorizationRead = AuthorizationPlc.findByCode(code);

        System.out.println(authorizationRead);

        if (plcTags.getAuthorizationPlc() != authorizationRead) {
            plcTags.setAuthorizationPlc(authorizationRead);
            handleAuthorizationPlcChange(authorizationRead);
        }
    }

    private void handleAuthorizationPlcChange(AuthorizationPlc authorizationRead) throws ExecutionException, InterruptedException {
        ConsumptionChannel consumptionChannel = null;

        switch (authorizationRead) {
            case AUTH_PLC_EMPTY:
                consumptionChannel = readOperationConsumptionData();
                mtexDriver.mtexProcessFinishedOrder(consumptionChannel);
                break;
            case AUTH_PLC_CANCELED:
                consumptionChannel = readOperationConsumptionData();
                mtexDriver.mtexProcessCanceledOrder(consumptionChannel);
                break;
            case AUTH_PLC_RETRY:
                mtexDriver.mtexResetAuthorization();
                break;
            case AUTH_PLC_ORDER_ID:
                mtexDriver.mtexCheckStartingConditions();
                break;
        }
    }

    private void processOperationMode() throws ExecutionException, InterruptedException {
        // TODO: Ler a tag OFFLINE no servidor OPC-UA -> mes.getNodeOPC().OFFLINE
        boolean code = (boolean) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().OFFLINE);

        OperationMode modeRead = OperationMode.findByCode(code);

        System.out.println(modeRead);

        if (plcTags.getOperationMode() != modeRead) {
            plcTags.setOperationMode(modeRead);

            switch (modeRead) {
                case OFFLINE:
                    //mtexDriver.mtexProcessOfflineOperationMode();
                    break;
                case ONLINE:
                    // TODO: Definir as tags que são para ler e o objeto criado
                    //ConsumptionChannel[] consumptionChannels = readOperationConsumptionData();
                    //mtexDriver.mtexProcessOnlineOperationMode(consumptionChannels[0], consumptionChannels[1]);
                    break;
            }
        }
    }

    private ConsumptionChannel readOperationConsumptionData() throws ExecutionException, InterruptedException {
        Object[] arrayConsumptionChannel = (Object[]) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().ARRAY_CANAL_CONSUMO_0);

        ConsumptionChannel consumptionChannel = new ConsumptionChannel(arrayConsumptionChannel);

        // TODO: Verificar se os consumos não são nulos antes de se criar uma instância do ConsumptionChannel
        return consumptionChannel;
    }
}
