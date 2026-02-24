package eu.wodrobina.rodos.rpc;

public record JsonRpcResponse(String jsonrpc, Object id, Object result, JsonRpcError error) {
    public static JsonRpcResponse ok(Object id, Object result) {
        return new JsonRpcResponse("2.0", id, result, null);
    }

    public static JsonRpcResponse error(Object id, JsonRpcError error) {
        return new JsonRpcResponse("2.0", id, null, error);
    }
}
