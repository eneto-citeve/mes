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
        } catch (ExecutionException exExc) {
            System.out.println("Error while reading tag OFFLINE: "+ exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while reading tag OFFLINE: "+ itExc);
        } catch (NullPointerException nlExc) {
            System.out.println("Error while reading tag OFFLINE: "+ nlExc);
        }
    }

    // Vai procurar pelos triggers (BATCHLOAD_PLC e AUTORIZACAO_PLC)
    @Override
    public void run() {
        try {
            synchronized (mtexDriver) {
                try {
                    processBatchloadPlc();
                } catch (ExecutionException exExc) {
                    System.out.println("Error while reading tag BATCHLOAD_PLC: "+ exExc);
                } catch (InterruptedException itExc) {
                    System.out.println("Error while reading tag BATCHLOAD_PLC: "+ itExc);
                } catch (NullPointerException nlExc) {
                    System.out.println("Error while reading tag BATCHLOAD_PLC: "+ nlExc);
                }

                // Tenho que ler o Auth
                try {
                    processAuthorizationPlc();
                } catch (ExecutionException exExc) {
                    System.out.println("Error while reading tag AUTORIZACAO_PLC: "+ exExc);
                } catch (InterruptedException itExc) {
                    System.out.println("Error while reading tag AUTORIZACAO_PLC: "+ itExc);
                } catch (NullPointerException nlExc) {
                    System.out.println("Error while reading tag AUTORIZACAO_PLC: "+ nlExc);
                }
            }
        } catch (Throwable t) {
            System.out.println("MES from MtexController: " + t.getMessage());
        }

    }

    private void processBatchloadPlc() throws ExecutionException, InterruptedException {
        // TODO: Ler a tag BATCHLOAD_PLC do servidor OPC-UA -> mes.getNodeOPC().BATCHLOAD_PLC
        int code = Integer.valueOf((short) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().BATCHLOAD_PLC));
        System.out.println(code);
        BatchloadPlc batchloadRead = BatchloadPlc.findByCode(code);

        System.out.println(batchloadRead);

        if (plcTags.getBatchloadPlc() != batchloadRead) {
            plcTags.setBatchloadPlc(batchloadRead);

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
    }

    private void processAuthorizationPlc() throws ExecutionException, InterruptedException {
        // TODO: Ler a tag AUTHORIZATION_PLC no servidor OPC-UA -> mes.getNodeOPC().AUTORIZACAO_PLC
        int code = Integer.valueOf((short) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().AUTORIZACAO_PLC));

        AuthorizationPlc authorizationRead = AuthorizationPlc.findByCode(code);

        System.out.println(authorizationRead);

        if (plcTags.getAuthorizationPlc() != authorizationRead) {
            plcTags.setAuthorizationPlc(authorizationRead);

            ConsumptionChannel[] consumptionChannels = null;

            switch (authorizationRead) {
                case AUTH_PLC_EMPTY:
                    consumptionChannels = readOperationConsumptionData();
                    mtexDriver.mtexProcessFinishedOrder(consumptionChannels[0], consumptionChannels[1]);
                    break;
                case AUTH_PLC_CANCELED:
                    consumptionChannels = readOperationConsumptionData();
                    mtexDriver.mtexProcessCanceledOrder(consumptionChannels[0], consumptionChannels[1]);
                    break;
                case AUTH_PLC_RETRY:
                    mtexDriver.mtexResetAuthorization();
                    break;
                case AUTH_PLC_ORDER_ID:
                    mtexDriver.mtexCheckStartingConditions();
                    break;
            }
        }
    }

    private void processOperationMode() throws ExecutionException, InterruptedException {
        // TODO: Ler a tag OFFLINE no servidor OPC-UA -> mes.getNodeOPC().OFFLINE
        boolean code = false; //(boolean) this.mtexDriver.getOpcConnector().readNode(this.mtexDriver.getNodeOPC().OFFLINE);

        OperationMode modeRead = OperationMode.findByCode(code);

        System.out.println(modeRead);

        if (plcTags.getOperationMode() != modeRead) {
            plcTags.setOperationMode(modeRead);

            switch (modeRead) {
                case OFFLINE:
                    mtexDriver.mtexProcessOfflineOperationMode();
                    break;
                case ONLINE:
                    // TODO: Definir as tags que são para ler e o objeto criado
                    ConsumptionChannel[] consumptionChannels = readOperationConsumptionData();
                    mtexDriver.mtexProcessOnlineOperationMode(consumptionChannels[0], consumptionChannels[1]);
                    break;
            }
        }
    }

    private ConsumptionChannel[] readOperationConsumptionData() {
        // TODO: Ler as tags dos canais de consumo -> mes.getNodeOPC().CONSUMO e mes.getNodeOPC().CONSUMO_TEORICO
        ConsumptionChannel[] consumptionChannels = new ConsumptionChannel[2];

        int realInkUsageMagenta = 0, realInkUsageBlack = 0, realInkUsageYellow = 0, realInkUsageCyan = 0, realRapportLength = 0;
        consumptionChannels[0] = new ConsumptionChannel(realInkUsageMagenta, realInkUsageBlack, realInkUsageYellow, realInkUsageCyan, realRapportLength);

        int estimatedInkUsageMagenta = 0, estimatedInkUsageBlack = 0, estimatedInkUsageYellow = 0, estimatedInkUsageCyan = 0, estimatedRapportLength = 0;
        consumptionChannels[1] = new ConsumptionChannel(estimatedInkUsageMagenta, estimatedInkUsageBlack, estimatedInkUsageYellow, estimatedInkUsageCyan, estimatedRapportLength);

        // TODO: Verificar se os consumos não são nulos antes de se criar uma instância do ConsumptionChannel
        return consumptionChannels;
    }
}
