package eu.wodrobina.rodos.rpc;

import java.util.Map;

public record JsonRpcRequest(String jsonrpc, Object id, String method, Map<String, Object> params) {
}
