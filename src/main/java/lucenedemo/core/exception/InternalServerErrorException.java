package lucenedemo.core.exception;

/**
 * 服务端内部异常
 * 
 * @author zrx
 */
public class InternalServerErrorException extends BusinessException {

	private static final long serialVersionUID = -5197049627396589939L;

	public InternalServerErrorException() {
		super(500, "Internal Server Error");
	}

	public InternalServerErrorException(String message) {
		super(500, message);
	}

}
