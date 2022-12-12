package opcuaConnector;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class Node {
    public final NodeId node;
    public final Type type;

    public Node(int namespace, String tagName, Type type) {
        this.node = new NodeId(namespace, tagName);
        this.type = type;
    }
}
