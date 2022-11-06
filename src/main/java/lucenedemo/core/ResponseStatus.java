package lucenedemo.core;

/**
 * 响应状态枚举
 *
 * @author zrx
 */
public enum ResponseStatus {

	/**
	 * 成功
	 */
	SUCCESS(200, 200, "yes", "成功", "成功"),

	/**
	 * 失败
	 */
	FAILED(-200, 417, "no", "失败", "异常失败"),

	/**
	 * 业务异常
	 */
	BUSINESS_EXCEPTION(2000, 200, "no", "网络连接不稳~请稍后再试", "业务异常"),

	/**
	 * 网络请求异常
	 */
	HTTP_CLIENT_EXCEPTION(4000, 400, "no", "网络连接不稳~请稍后再试", "网络请求异常: 第三方业务内部异常，请检查所访问的第三方业务是否正常"),

	/**
	 * 网络请求异常:第三方服务连接异常：请检查访问路径是否正常
	 */
	HTTP_CLIENT_CONNECTION_EXCEPTION(4004, 400, "no", "网络连接不稳~请稍后再试", "网络请求异常:第三方服务连接异常,请检查第三方访问路径是否正常"),

	/**
	 * 数据库异常
	 */
	DATA_ACCESS_EXCEPTION(6000, 500, "no", "服务器开小差~请稍后再试", "sql异常"),

	/**
	 * Valid参数校验异常
	 */
	VALID_EXCEPTION(5000, 400, "no", "服务器开小差~请稍后再试", "Valid参数校验异常"),

	/**
	 * 栈溢出
	 */
	STACK_OVERFLOW_ERROR(5001, 500, "no", "服务器开小差~请稍后再试", "栈溢出"),

	/**
	 * 其他错误
	 */
	OTHER_EXCEPTION(5002, 500, "no", "服务器开小差~请稍后再试", "其他错误"),

	/**
	 * 类型转换不支持异常
	 */
	CONVERSION_NOT_SUPPORTED_EXCEPTION(5003, 500, "no", "服务器开小差~请稍后再试", "类型转换不支持异常"),

	/**
	 * io异常
	 */
	IOException(5004, 500, "no", "服务器开小差~请稍后再试", "io异常"),

	/**
	 * 空指针异常
	 */
	NULL_POINTER_EXCEPTION(5005, 500, "no", "服务器开小差~请稍后再试", "空指针异常,请检查参数是否为空"),

	/**
	 * 未知方法异常
	 */
	NO_SUCH_METHOD_EXCEPTION(5006, 500, "no", "服务器开小差~请稍后再试", "未知方法异常"),

	/**
	 * 数组越界异常
	 */
	INDEX_OUT_OF_BOUNDS_EXCEPTION(5007, 500, "no", "服务器开小差~请稍后再试", "数组越界异常,请检查数组大小"),

	/**
	 * 无法找到该资源
	 */
	NOT_FOUND_EXCEPTION(5008, 500, "no", "无法找到该资源", "无法找到该资源"),

	/**
	 * 无授权访问，请先登录
	 */
	NOT_AUTHORIZED_EXCEPTION(401, 401, "no", "无授权访问，请先登录", "无授权访问，请先登录"),

	/**
	 * 类型不匹配异常
	 */
	TYPE_MISMATCH_EXCEPTION(400, 400, "no", "服务器开小差~请稍后再试", "类型不匹配异常"),

	/**
	 * 类型不匹配异常
	 */
	MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(400, 400, "no", "服务器开小差~请稍后再试", "类型不匹配异常"),

	/**
	 * http媒体类型不支持异常
	 */
	NOT_ACCEPTABLE(406, 406, "no", "服务器开小差~请稍后再试", "http媒体类型不支持异常"),

	/**
	 * 方法不允许
	 */
	METHOD_NOT_ALLOWED(405, 405, "no", "方法不允许", "方法不允许"),

	/**
	 * 运行时异常
	 */
	RUNTIME_EXCEPTION(500, 500, "no", "服务器开小差~请稍后再试", "运行时异常"),

	/**
	 * 类型转换异常
	 */
	CLASS_CAST_EXCEPTION(5009, 500, "no", "服务器开小差~请稍后再试", "类型转换异常");

	/**
	 * 业务code
	 */
	public Integer bCode;
	/**
	 * http code
	 */
	public Integer hCode;
	/**
	 * 英文状态值
	 */
	public String valueEn;
	/**
	 * 返回给前端的中文说明
	 */
	public String valueZh;
	/**
	 * 存储日志的中文说明
	 */
	public String valueLog;

	ResponseStatus(Integer bCode, Integer hCode, String valueEn, String valueZh, String valueLog) {
		this.bCode = bCode;
		this.hCode = hCode;
		this.valueEn = valueEn;
		this.valueZh = valueZh;
		this.valueLog = valueLog;
	}

}
