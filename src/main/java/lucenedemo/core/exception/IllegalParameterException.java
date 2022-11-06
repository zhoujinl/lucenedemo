package lucenedemo.core.exception;

/**
 * 违法参数异常
 *
 * @author zrx
 */
public class IllegalParameterException extends BusinessException {

	private static final long serialVersionUID = -4630300924643755046L;


	public IllegalParameterException() {
		super(500, "Internal Server Error: illegal parameter");
	}

	public IllegalParameterException(String message) {
		super(500, message);
	}

}
