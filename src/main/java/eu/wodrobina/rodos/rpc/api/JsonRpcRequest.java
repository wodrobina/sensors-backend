package eu.wodrobina.rodos.rpc.api;

import java.util.Map;

public record JsonRpcRequest(String jsonrpc, Object id, String method, Map<String, Object> params) {
}
