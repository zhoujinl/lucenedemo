package lucenedemo.core.exception;

/**
 * 网络请求连接异常
 *
 * @author zrx
 */
public class HttpClientConnectionException extends BusinessException {

	private static final long serialVersionUID = -4630300924643755046L;


	public HttpClientConnectionException() {
		super(4004, "Http Client Connection Exception");
	}

	public HttpClientConnectionException(String message) {
		super(4004, message);
	}

}
