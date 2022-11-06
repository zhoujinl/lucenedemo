package lucenedemo.core.exception;


import lucenedemo.core.ResponseStatus;

/**
 * 业务异常
 * 由系统内部业务决定，属于主动抛出的异常
 *
 * @author zrx
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 8094428458440697386L;
	
	private int code = ResponseStatus.BUSINESS_EXCEPTION.bCode;

	public BusinessException() {
		super();
	}

	public BusinessException(int code) {
		this.code = code;
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}