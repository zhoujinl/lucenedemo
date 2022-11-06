package lucenedemo.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求响应体
 *
 * @author zrx
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult implements Serializable {

	private static final long serialVersionUID = 8041766238120354183L;

	private int code;
	private String status;
	private String msg;
	private Object data;

	public static ResponseResult success() {
		return success(null);
	}

	public static ResponseResult success(Object data) {
		return ResponseResult.builder()
				.status(ResponseStatus.SUCCESS.valueEn)
				.msg(ResponseStatus.SUCCESS.valueZh)
				.code(ResponseStatus.SUCCESS.bCode)
				.data(data)
				.build();
	}

	public static ResponseResult failed() {
		return failed("失败");
	}

	public static ResponseResult failed(String msg) {
		return failed(ResponseStatus.FAILED.bCode, msg);
	}

	public static ResponseResult failed(int code, String msg) {
		return ResponseResult.builder()
				.status(ResponseStatus.FAILED.valueEn)
				.msg(msg)
				.code(code)
				.build();
	}

}