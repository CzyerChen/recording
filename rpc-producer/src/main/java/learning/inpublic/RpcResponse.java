package learning.inpublic;

import java.io.Serializable;

/**
 * 响应类
 */
public class RpcResponse implements Serializable{
	private final long invokeId;
	public RpcResponse(RpcRequest request) {
		invokeId = request.getInvokeId();
	}
	private Object result;

	public long getInvokeId() {
		return invokeId;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "RpcResponse{" +
				"invokeId=" + invokeId +
				", result=" + result +
				'}';
	}
}
