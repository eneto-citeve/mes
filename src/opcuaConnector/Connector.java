package opcuaConnector;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class Connector {
    private final OpcUaClient opcClient;
    private final String serverUri;

    public Connector(String ip, int port) throws UaException {
        this.serverUri = "opc.tcp://" + ip + ":" + port + "/PlugAndProduce";

        KeyStoreLoader keyLoader = new KeyStoreLoader();

        this.opcClient = OpcUaClient.create(
                this.serverUri,
                endpoints -> endpoints
                        .stream()
                        .findFirst(),
                configBuilder -> configBuilder
                            .setApplicationName(LocalizedText.english("MES Client"))
                            .setApplicationUri("urn:pps3:mes:client")
                            .setCertificate(keyLoader.getClientCertificate())
                            .setKeyPair(keyLoader.getClientKeyPair())
                            .setIdentityProvider(new AnonymousProvider())
                            .setRequestTimeout(uint(5000))
                            .build()
        );
    }

    public void writeNode(Node node, Object value) throws ExecutionException, InterruptedException {
        OpcUaClient client = this.opcClient;
        client.connect().get();

        StatusCode status = client.writeValue(node.node, new DataValue(new Variant(value), null, null))
                .get();

        System.out.println("From Heartbeat: " + status);
    }

    public Object readNode(Node node) throws ExecutionException, InterruptedException {
        OpcUaClient client = this.opcClient;
        client.connect().get();

        DataValue dataValue = client.readValue(0.0, TimestampsToReturn.Both, node.node)
                    .get();

        return dataValue.getValue().getValue();
    }
}

