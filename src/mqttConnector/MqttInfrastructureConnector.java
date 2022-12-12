package mqttConnector;

import com.google.gson.Gson;
import controllers.OrderStatus;
import controllers.OrderStatusResponse;
import controllers.StateOfOrder;
import controllers.StatusOfOrder;
import org.eclipse.paho.client.mqttv3.*;
import pps3.plugandproduce.mtex.orders.OrderStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MqttInfrastructureConnector extends Thread {
    private MqttClient clientInfrastructure;
    private Queue<OrderStructure> queueNewOrders;
    private Queue<OrderStatus> queueOrderStatus;

    private static final Map<StatusOfOrder, String> topicPublish = new HashMap<StatusOfOrder, String>() {{
        put(StatusOfOrder.REQUESTED, "mes/nextOrder/validate");
        put(StatusOfOrder.CREATED, "mes/execOrder");
        put(StatusOfOrder.FINISHED, "mes/finishOrder");
    }};

    public MqttInfrastructureConnector(Queue<OrderStructure> q1, Queue<OrderStatus> q2) {
        this.queueNewOrders = q1;
        this.queueOrderStatus = q2;
    }

    private class ModifiedMqttCallback implements MqttCallback {
        private Queue<OrderStructure> queueNewOrders;

        public ModifiedMqttCallback(Queue<OrderStructure> q1) {
            this.queueNewOrders = q1;
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("connectionLost: " + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            Gson gson = new Gson();
            OrderStructure newOrder = gson.fromJson(new String(message.getPayload()), OrderStructure.class);
            this.queueNewOrders.add(newOrder);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete---------" + token.isComplete());
        }
    }

    @Override
    public void run() {
        try {
            String brokerInfrastructure = "tcp://localhost:1883";
            String topicSubscribe = "mes/nextOrder/config";

            this.clientInfrastructure = new MqttClient(brokerInfrastructure, "mes-java-pps3");

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setKeepAliveInterval(30);

            this.clientInfrastructure.setCallback(new ModifiedMqttCallback(this.queueNewOrders));
            this.clientInfrastructure.connect(options);

            this.clientInfrastructure.subscribe(topicSubscribe);

            for (;;) {
                for (OrderStatus status = this.queueOrderStatus.poll(); status != null; status = this.queueOrderStatus.poll()) {
                    //System.out.println(status.orderId() + " " + status.state());

                    OrderStatusResponse response = new OrderStatusResponse(status.orderId(), StateOfOrder.getCode(status.state()));

                    Gson gson = new Gson();
                    String jsonMessage = gson.toJson(response);

                    MqttMessage mqttMessage = new MqttMessage(jsonMessage.getBytes());
                    this.clientInfrastructure.publish(topicPublish.get(status.status()), mqttMessage);
                }
            }

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
