package controllers;

import opcuaConnector.Connector;
import opcuaConnector.InformationModel;
import pps3.plugandproduce.mtex.MtexDriver;
import pps3.plugandproduce.mtex.exceptions.OvenTemperatureOutsideRange;
import pps3.plugandproduce.mtex.exceptions.OverprintOutsideRange;
import pps3.plugandproduce.mtex.exceptions.StepCountOutsideRange;
import pps3.plugandproduce.mtex.orders.ConsumptionChannel;
import pps3.plugandproduce.mtex.orders.OrderStructure;
import pps3.plugandproduce.mtex.orders.WordOrder;
import pps3.plugandproduce.mtex.tags.AuthorizationMes;
import pps3.plugandproduce.mtex.tags.BatchloadMes;
import pps3.plugandproduce.mtex.tags.MesTags;

import java.util.Queue;
import java.util.concurrent.ExecutionException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

public class MES implements MtexDriver {
    private WordOrder nextWorkOrder;
    private WordOrder currentWorkOrder;
    private MesTags mesTags;

    @Override
    public InformationModel getNodeOPC() {
        return nodeOPC;
    }

    public void setOpcConnector(Connector opcConnector) {
        if (this.opcConnector == null) {
            this.opcConnector = opcConnector;
        }
    }

    @Override
    public Connector getOpcConnector() {
        return opcConnector;
    }

    private Connector opcConnector;
    private InformationModel nodeOPC;

    private final Queue<OrderStatus> queueOrderStatus;

    public MES(Queue<OrderStatus> queueOrderStatus) {
        this.nextWorkOrder = null;
        this.currentWorkOrder = null;
        this.mesTags = new MesTags();
        this.queueOrderStatus = queueOrderStatus;
        this.nodeOPC = new InformationModel(1);
        this.opcConnector = null;
    }

    public WordOrder getNextWorkOrder() {
        return nextWorkOrder;
    }

    @Override
    public void mtexCreateNewOrder(OrderStructure newOrder) {

        try {
            this.nextWorkOrder = new WordOrder(
                    newOrder.getOrderId(),
                    newOrder.getQuantity(),
                    newOrder.getRipId(),
                    newOrder.getOverprint(),
                    newOrder.getStepCount(),
                    newOrder.getOvenTemperature()
            );

            queueOrderStatus.add(new OrderStatus(StatusOfOrder.REQUESTED, this.nextWorkOrder.getOrderId(), StateOfOrder.REQUESTED_ACCEPTED));

        } catch (OverprintOutsideRange | OvenTemperatureOutsideRange | StepCountOutsideRange overprintException) {
            queueOrderStatus.add(new OrderStatus(StatusOfOrder.REQUESTED, this.nextWorkOrder.getOrderId(), StateOfOrder.REQUESTED_DENIED));
        }

        if (checkIfBatchloadMesEmpty()) {
            mtexExecuteOrder();
        }
    }

    private boolean checkIfBatchloadMesEmpty() {
        return this.mesTags.getBatchloadMes() == BatchloadMes.BATCHLOAD_MES_EMPTY;
    }

    @Override
    public void mtexIncrementHeartbeat() {
        try {
            int heartbeat = this.mesTags.incrementHeartbeat();

            // TODO: Escrever na tag HEARTBEAT no servidor OPC-UA
            this.opcConnector.writeNode(this.nodeOPC.HEARTBEAT, uint(heartbeat));
            System.out.println(heartbeat);
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag HEARTBEAT: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag HEARTBEAT: " + itExc);
        }

    }

    @Override
    public void mtexExecuteOrder() {
        this.currentWorkOrder = this.nextWorkOrder;
        this.nextWorkOrder = null;

        if (this.currentWorkOrder != null) {
            // TODO: Escrever nova ordem no servidor OPC-UA

            // TODO: Informar via MQTT
            this.queueOrderStatus.add(new OrderStatus(StatusOfOrder.CREATED, this.currentWorkOrder.getOrderId(), StateOfOrder.CREATED_SETUP));

            //this.mesTags.getBatchloadMes(BatchloadMes.findByCode(this.currentWorkOrder.getOrderId()));
            try {
                this.opcConnector.writeNode(this.nodeOPC.BATCHLOAD_MES, this.mesTags.getBatchloadMes());
            }  catch (ExecutionException exExc) {
                System.out.println("Error while writing on tag BATCHLOAD_MES: " + exExc);
            } catch (InterruptedException itExc) {
                System.out.println("Error while writing on tag BATCHLOAD_MES: " + itExc);
            }
        }
    }

    public void setTimeWithoutHeartbeat() {
        try {
            this.opcConnector.writeNode(this.nodeOPC.TEMPO_SEM_HEARTBEAT, ushort(MesTags.getTimeWithoutHeartbeat()));
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag TEMPO_SEM_HEARTBEAT: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag TEMPO_SEM_HEARTBEAT: " + itExc);
        }

    }

    @Override
    public void mtexProcessOfflineOperationMode() {
        // TODO: Escrever na tag LER_HISTORICO (1) no servidor OPC-UA
        try {
            this.opcConnector.writeNode(nodeOPC.LER_HISTORICO, true);
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag LER_HISTORICO: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag LER_HISTORICO: " + itExc);
        }
    }

    @Override
    public void mtexProcessOnlineOperationMode(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption) {
        mtexProcessHistoricalData(realConsumption, estimatedConsumption);
        // TODO: Escrever na tag LER_HISTORICO (0) no servidor OPC-UA
        try {
            this.opcConnector.writeNode(nodeOPC.LER_HISTORICO, false);
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag LER_HISTORICO: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag LER_HISTORICO: " + itExc);
        }
    }

    // TODO: Definir os argumentos da função
    private void mtexProcessHistoricalData(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption) {
        this.currentWorkOrder.setRealConsumption(realConsumption);
        this.currentWorkOrder.setEstimatedConsumption(estimatedConsumption);
    }

    @Override
    public void mtexProcessCanceledOrder(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption) {
        mtexProcessHistoricalData(realConsumption, estimatedConsumption);
        // TODO: Ler toda a informação sobre consumos e afins

        mesTags.setAuthorizationMes(AuthorizationMes.AUTH_MES_EMPTY);
        // TODO: Escrever na tag AUTORIZACAO_MES no servidor OPC_UA
        try {
            this.opcConnector.writeNode(nodeOPC.AUTORIZACAO_MES, AuthorizationMes.getCode(mesTags.getAuthorizationMes()));
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag LER_HISTORICO: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag LER_HISTORICO: " + itExc);
        }

        // TODO: Informar via MQTT
        this.queueOrderStatus.add(new OrderStatus(StatusOfOrder.FINISHED, this.currentWorkOrder.getOrderId(), StateOfOrder.FINISHED_CANCELED));
    }

    @Override
    public void mtexProcessFinishedOrder(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption) {
        mtexProcessHistoricalData(realConsumption, estimatedConsumption);
        // TODO: Ler toda a informação sobre consumos e afins

        mesTags.setAuthorizationMes(AuthorizationMes.AUTH_MES_EMPTY);
        // TODO: Escrever na tag AUTORIZACAO_MES no servidor OPC_UA

        // TODO: Informar via MQTT
        this.queueOrderStatus.add(new OrderStatus(StatusOfOrder.FINISHED, this.currentWorkOrder.getOrderId(), StateOfOrder.FINISHED_SUCCESSFULLY));

        // Ver se isto está correto
        mtexExecuteOrder();
    }

    @Override
    public void mtexCheckIfSameOrderId(int orderId) {
        if (orderId == BatchloadMes.getCode(mesTags.getBatchloadMes())) {
            mesTags.setBatchloadMes(BatchloadMes.BATCHLOAD_MES_EMPTY);

            // TODO: Escrever na tag no servidor OPC-UA
            try {
                this.opcConnector.writeNode(nodeOPC.BATCHLOAD_MES, BatchloadMes.getCode(mesTags.getBatchloadMes()));
            } catch (ExecutionException exExc) {
                System.out.println("Error while writing on tag BATCHLOAD_MES: " + exExc);
            } catch (InterruptedException itExc) {
                System.out.println("Error while writing on tag BATCHLOAD_MES: " + itExc);
            }
        }
    }

    @Override
    public void mtexResetWorkOrder(String message) {
        mesTags.setBatchloadMes(BatchloadMes.BATCHLOAD_MES_EMPTY);

        // TODO: Escrever na tag no servidor OPC-UA
        try {
            this.opcConnector.writeNode(nodeOPC.BATCHLOAD_MES, BatchloadMes.getCode(mesTags.getBatchloadMes()));
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag BATCHLOAD_MES: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag BATCHLOAD_MES: " + itExc);
        }
    }

    @Override
    public void mtexResetAuthorization() {
        mesTags.setAuthorizationMes(AuthorizationMes.AUTH_MES_EMPTY);

        // TODO: Escrever na tag no servidor OPC-UA
        try {
            this.opcConnector.writeNode(nodeOPC.AUTORIZACAO_MES, AuthorizationMes.getCode(mesTags.getAuthorizationMes()));
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag AUTORIZACAO_MES: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag AUTORIZACAO_MES: " + itExc);
        }
    }

    @Override
    public void mtexCheckStartingConditions() {
        // TODO: Definir isto
        boolean startingConditions = true;
        int orderId = 9999;

        if (startingConditions) {
            mesTags.setAuthorizationMes(AuthorizationMes.findByCode(orderId));
        }
        else {
            mesTags.setAuthorizationMes(AuthorizationMes.AUTH_MES_NO_AUTHORIZATION);
        }

        // TODO: Enviar para o servidor OPC-UA
        try {
            this.opcConnector.writeNode(nodeOPC.AUTORIZACAO_MES, AuthorizationMes.getCode(mesTags.getAuthorizationMes()));
        } catch (ExecutionException exExc) {
            System.out.println("Error while writing on tag AUTORIZACAO_MES: " + exExc);
        } catch (InterruptedException itExc) {
            System.out.println("Error while writing on tag AUTORIZACAO_MES: " + itExc);
        }
    }
}
