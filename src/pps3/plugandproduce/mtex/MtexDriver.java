package pps3.plugandproduce.mtex;

import opcuaConnector.Connector;
import opcuaConnector.InformationModel;
import pps3.plugandproduce.mtex.orders.ConsumptionChannel;
import pps3.plugandproduce.mtex.orders.OrderStructure;

public interface MtexDriver {
    void mtexCreateNewOrder(OrderStructure newOrder);
    void mtexIncrementHeartbeat();
    void mtexExecuteOrder();
    void mtexCheckIfSameOrderId(int orderId);
    void mtexProcessOfflineOperationMode();
    void mtexProcessOnlineOperationMode(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption);
    // void mtexProcessHistoricalData(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption);
    void mtexProcessCanceledOrder(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption);
    void mtexProcessFinishedOrder(ConsumptionChannel realConsumption, ConsumptionChannel estimatedConsumption);
    void mtexResetWorkOrder(String message);
    void mtexResetAuthorization();
    void mtexCheckStartingConditions();
    Connector getOpcConnector();
    InformationModel getNodeOPC();
}
