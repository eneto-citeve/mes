import controllers.*;
import mqttConnector.*;
import opcuaConnector.Connector;
import org.eclipse.milo.opcua.stack.core.UaException;
import pps3.plugandproduce.mtex.MtexController;
import pps3.plugandproduce.mtex.exceptions.OvenTemperatureOutsideRange;
import pps3.plugandproduce.mtex.exceptions.OverprintOutsideRange;
import pps3.plugandproduce.mtex.exceptions.StepCountOutsideRange;
import pps3.plugandproduce.mtex.orders.OrderStructure;
import pps3.plugandproduce.mtex.tags.BatchloadPlc;
import pps3.plugandproduce.mtex.tags.MesTags;
import pps3.plugandproduce.mtex.tags.PlcTags;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Queue<OrderStructure> queueNewOrders = new ConcurrentLinkedQueue<OrderStructure>();
    public static Queue<OrderStatus> queueOrderStatus = new ConcurrentLinkedQueue<OrderStatus>();

    public static void main(String[] args) throws InterruptedException, UaException {
        MqttInfrastructureConnector mqttConn = new MqttInfrastructureConnector(queueNewOrders, queueOrderStatus);
        mqttConn.start();

        MES mes = new MES(queueOrderStatus);

        String ip = "localhost";
        int port = 55380;
        Connector opcConnector = new Connector(ip, port);
        mes.setOpcConnector(opcConnector);

        MtexController mtexController = new MtexController(mes);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
        scheduler.scheduleAtFixedRate(new CreateOrder(mes, queueNewOrders), 0, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new Heartbeat(mes), 0, MesTags.getTimeWithoutHeartbeat() - 5, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(mtexController, 0, 1, TimeUnit.SECONDS);
    }

    private static class CreateOrder extends Thread {
        private final MES mes;
        private final Queue<OrderStructure> queueNewOrders;

        public CreateOrder(MES mes, Queue<OrderStructure> q1) {
            this.mes = mes;
            this.queueNewOrders = q1;
        }

        @Override
        public void run() {
            OrderStructure newOrder = null;

            try {
                synchronized (mes) {
                    for (newOrder = queueNewOrders.poll(); newOrder != null; newOrder = queueNewOrders.poll()) {
                        System.out.println(newOrder);
                        mes.mtexCreateNewOrder(newOrder);
                    }
                }
            } catch (Throwable t) {
                System.out.println("MES from CreateOrder: " + t.getMessage());
            }
        }
    }

    private static class Heartbeat extends Thread {
        private MES mes;

        public Heartbeat(MES mes) {
            this.mes = mes;
            this.mes.setTimeWithoutHeartbeat();
        }

        @Override
        public void run() {
            try {
                synchronized (mes) {
                    mes.mtexIncrementHeartbeat();
                }
            } catch (Throwable t) {
                System.out.println("MES from Heartbeat: " + t.getMessage());
            }
        }
    }
}