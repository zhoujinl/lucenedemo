package lucenedemo.core.exception;


/**
 * 禁止访问异常
 *
 * @author zrx
 */
public class ForbiddenException extends BusinessException {

	private static final long serialVersionUID = -4630300924643755046L;

	public ForbiddenException() {
		super(403, "Access Forbidden");
	}

	public ForbiddenException(String message) {
		super(403, message);
	}

}
