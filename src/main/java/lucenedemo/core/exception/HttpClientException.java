package lucenedemo.core.exception;

/**
 * 网络请求异常
 *
 * @author zrx
 */
public class HttpClientException extends BusinessException {

	private static final long serialVersionUID = -4630300924643755046L;


	public HttpClientException() {
		super(4000, "Http Client Exception");
	}

	public HttpClientException(String message) {
		super(4000, message);
	}

}
