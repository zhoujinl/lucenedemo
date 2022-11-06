package lucenedemo.config;

import lombok.extern.slf4j.Slf4j;
import lucenedemo.core.ResponseResult;
import lucenedemo.core.ResponseStatus;
import lucenedemo.core.annotation.NoPack;
import lucenedemo.core.exception.BusinessException;
import lucenedemo.core.exception.ForbiddenException;
import lucenedemo.core.exception.HttpClientConnectionException;
import lucenedemo.core.exception.HttpClientException;
import lucenedemo.core.exception.NotAuthorizedException;
import lucenedemo.core.exception.NotFoundException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 全局异常拦截处理器
 *
 * @author zrx
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class AllExceptionHandler implements ResponseBodyAdvice {

	/**
	 * 网络请求异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(HttpClientConnectionException.class)
	public ResponseResult httpClientConnectionExceptionHandler(HttpServletResponse response, HttpClientConnectionException ex) {
		log.error(ResponseStatus.HTTP_CLIENT_CONNECTION_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.HTTP_CLIENT_CONNECTION_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.HTTP_CLIENT_CONNECTION_EXCEPTION.bCode, ResponseStatus.HTTP_CLIENT_CONNECTION_EXCEPTION.valueZh);
	}

	/**
	 * 网络请求异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(HttpClientException.class)
	public ResponseResult httpClientExceptionHandler(HttpServletResponse response, HttpClientException ex) {
		log.error(ex.getMessage(), ex);
		response.setStatus(ResponseStatus.RUNTIME_EXCEPTION.bCode);
		return ResponseResult.failed(ResponseStatus.HTTP_CLIENT_EXCEPTION.bCode, ResponseStatus.HTTP_CLIENT_EXCEPTION.valueZh);
	}

	/**
	 * 数据库异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DataAccessException.class)
	public ResponseResult dataAccessExceptionHandler(HttpServletResponse response, DataAccessException ex) {
		log.error(ResponseStatus.DATA_ACCESS_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.DATA_ACCESS_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.DATA_ACCESS_EXCEPTION.bCode, ResponseStatus.DATA_ACCESS_EXCEPTION.valueZh);
	}

	/**
	 * 无授权访问异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(NotAuthorizedException.class)
	public ResponseResult businessExceptionHandler(HttpServletResponse response, NotAuthorizedException ex) {
		log.error(ResponseStatus.NOT_AUTHORIZED_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.NOT_AUTHORIZED_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.NOT_AUTHORIZED_EXCEPTION.bCode, null == ex.getMessage() ? ResponseStatus.NOT_AUTHORIZED_EXCEPTION.valueZh : ex.getMessage());
	}

	/**
	 * 普通业务异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseResult businessExceptionHandler(HttpServletResponse response, BusinessException ex) {
		log.error(ex.getMessage(), ex);
		response.setStatus(ResponseStatus.BUSINESS_EXCEPTION.hCode);
		return ResponseResult.failed(ex.getCode(), ex.getMessage());
	}

	/**
	 * 无资源异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(NotFoundException.class)
	public ResponseResult notFoundExceptionHandler(HttpServletResponse response, NotFoundException ex) {
		log.error(ResponseStatus.NOT_FOUND_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.NOT_FOUND_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.NOT_FOUND_EXCEPTION.bCode, ResponseStatus.NOT_FOUND_EXCEPTION.valueZh);
	}

	/**
	 * 无权访问异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ForbiddenException.class)
	public ResponseResult forbiddenExceptionHandler(HttpServletResponse response, ForbiddenException ex) {
		log.error(ResponseStatus.NOT_AUTHORIZED_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.NOT_AUTHORIZED_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.NOT_AUTHORIZED_EXCEPTION.bCode, null == ex.getMessage() ? ResponseStatus.NOT_AUTHORIZED_EXCEPTION.valueZh : ex.getMessage());
	}

	/**
	 * 运行时异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseResult runtimeExceptionHandler(HttpServletResponse response, RuntimeException ex) {
		log.error(ResponseStatus.RUNTIME_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.RUNTIME_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.RUNTIME_EXCEPTION.bCode, ResponseStatus.RUNTIME_EXCEPTION.valueZh);
	}

	/**
	 * Validate异常
	 *
	 * @param ex
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseResult bindExceptionHandler(HttpServletResponse response, MethodArgumentNotValidException ex) {
		StringBuilder errorMessage = new StringBuilder();
		ex.getBindingResult().getAllErrors().forEach(x ->
				errorMessage.append(x.getDefaultMessage()).append(",")
		);

		log.error(ResponseStatus.VALID_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.VALID_EXCEPTION.hCode);

		return ResponseResult.failed(ResponseStatus.VALID_EXCEPTION.bCode, errorMessage.toString().substring(0, errorMessage.length() - 1));
	}

	/**
	 * 空指针异常
	 *
	 * @param ex
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(NullPointerException.class)
	public ResponseResult nullPointerExceptionHandler(HttpServletResponse response, NullPointerException ex) {
		log.error(ResponseStatus.NULL_POINTER_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.NULL_POINTER_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.NULL_POINTER_EXCEPTION.bCode, ResponseStatus.NULL_POINTER_EXCEPTION.valueZh);
	}

	/**
	 * 类型转换异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ClassCastException.class)
	public ResponseResult classCastExceptionHandler(HttpServletResponse response, ClassCastException ex) {
		log.error(ResponseStatus.CLASS_CAST_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.CLASS_CAST_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.CLASS_CAST_EXCEPTION.bCode, ResponseStatus.CLASS_CAST_EXCEPTION.valueZh);
	}

	/**
	 * IO异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(IOException.class)
	public ResponseResult iOExceptionHandler(HttpServletResponse response, IOException ex) {
		log.error(ResponseStatus.IOException.valueLog, ex);
		response.setStatus(ResponseStatus.IOException.hCode);
		return ResponseResult.failed(ResponseStatus.IOException.bCode, ResponseStatus.IOException.valueZh);
	}

	/**
	 * 未知方法异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(NoSuchMethodException.class)
	public ResponseResult noSuchMethodExceptionHandler(HttpServletResponse response, NoSuchMethodException ex) {
		log.error(ResponseStatus.NO_SUCH_METHOD_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.NO_SUCH_METHOD_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.NO_SUCH_METHOD_EXCEPTION.bCode, ResponseStatus.NO_SUCH_METHOD_EXCEPTION.valueZh);
	}

	/**
	 * 数组越界异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public ResponseResult indexOutOfBoundsExceptionHandler(HttpServletResponse response, IndexOutOfBoundsException ex) {
		log.error(ResponseStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION.bCode, ResponseStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION.valueZh);
	}

	/**
	 * 400错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseResult requestNotReadable(HttpServletResponse response, HttpMessageNotReadableException ex) {
		log.error(ResponseStatus.METHOD_NOT_ALLOWED.valueLog, ex);
		response.setStatus(ResponseStatus.METHOD_NOT_ALLOWED.hCode);
		return ResponseResult.failed(ResponseStatus.METHOD_NOT_ALLOWED.bCode, ResponseStatus.METHOD_NOT_ALLOWED.valueZh);
	}

	/**
	 * 400错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({TypeMismatchException.class})
	public ResponseResult requestTypeMismatch(HttpServletResponse response, TypeMismatchException ex) {
		log.error(ResponseStatus.TYPE_MISMATCH_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.TYPE_MISMATCH_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.TYPE_MISMATCH_EXCEPTION.bCode, ResponseStatus.TYPE_MISMATCH_EXCEPTION.valueZh);
	}

	/**
	 * 400错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({MissingServletRequestParameterException.class})
	public ResponseResult requestMissingServletRequest(HttpServletResponse response, MissingServletRequestParameterException ex) {
		log.error(ResponseStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.bCode, ResponseStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.valueZh);
	}

	/**
	 * 405错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	public ResponseResult request405(HttpServletResponse response, HttpRequestMethodNotSupportedException ex) {
		log.error(ResponseStatus.METHOD_NOT_ALLOWED.valueLog, ex);
		response.setStatus(ResponseStatus.METHOD_NOT_ALLOWED.hCode);
		return ResponseResult.failed(ResponseStatus.METHOD_NOT_ALLOWED.bCode, ResponseStatus.METHOD_NOT_ALLOWED.valueZh);
	}

	/**
	 * 406错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
	public ResponseResult request406(HttpServletResponse response, HttpMediaTypeNotAcceptableException ex) {
		log.error(ResponseStatus.NOT_ACCEPTABLE.valueLog, ex);
		response.setStatus(ResponseStatus.NOT_ACCEPTABLE.hCode);
		return ResponseResult.failed(ResponseStatus.NOT_ACCEPTABLE.bCode, ResponseStatus.NOT_ACCEPTABLE.valueZh);
	}

	/**
	 * 500错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
	public ResponseResult server500(HttpServletResponse response, ConversionNotSupportedException ex) {
		log.error(ResponseStatus.CONVERSION_NOT_SUPPORTED_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.CONVERSION_NOT_SUPPORTED_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.CONVERSION_NOT_SUPPORTED_EXCEPTION.bCode, ResponseStatus.CONVERSION_NOT_SUPPORTED_EXCEPTION.valueZh);
	}

	/**
	 * 栈溢出
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({StackOverflowError.class})
	public ResponseResult requestStackOverflow(HttpServletResponse response, StackOverflowError ex) {
		log.error(ResponseStatus.STACK_OVERFLOW_ERROR.valueLog, ex);
		response.setStatus(ResponseStatus.STACK_OVERFLOW_ERROR.hCode);
		return ResponseResult.failed(ResponseStatus.STACK_OVERFLOW_ERROR.bCode, ResponseStatus.STACK_OVERFLOW_ERROR.valueZh);
	}

	/**
	 * 非法参数
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseResult requestIllegalArgumentException(HttpServletResponse response, IllegalArgumentException ex) {
		log.error(ResponseStatus.BUSINESS_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.BUSINESS_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.BUSINESS_EXCEPTION.bCode, ex.getMessage());
	}

	/**
	 * 方法不允许
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({MethodNotAllowedException.class})
	public ResponseResult requestMethodNotAllowedException(HttpServletResponse response, MethodNotAllowedException ex) {
		log.error(ResponseStatus.METHOD_NOT_ALLOWED.valueLog, ex);
		response.setStatus(ResponseStatus.METHOD_NOT_ALLOWED.hCode);
		return ResponseResult.failed(ResponseStatus.METHOD_NOT_ALLOWED.bCode, ex.getMessage());
	}

	/**
	 * 其他错误
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler({Exception.class})
	public ResponseResult exception(HttpServletResponse response, Exception ex) {
		log.error(ResponseStatus.OTHER_EXCEPTION.valueLog, ex);
		response.setStatus(ResponseStatus.OTHER_EXCEPTION.hCode);
		return ResponseResult.failed(ResponseStatus.OTHER_EXCEPTION.bCode, ResponseStatus.OTHER_EXCEPTION.valueZh);
	}

	@Override
	public boolean supports(MethodParameter returnType, Class clazz) {
		return null == returnType.getMethodAnnotation(NoPack.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (MediaType.APPLICATION_JSON.equals(selectedContentType) || MediaType.APPLICATION_JSON_UTF8.equals(selectedContentType)) {
			Method method = (Method) returnType.getExecutable();
			if (ResponseEntity.class.equals(method.getReturnType())) {
				return body;
			}
			if (null == body) {
				return ResponseResult.success();
			}
			if (body instanceof ResponseResult) {
				return body;
			}
			return ResponseResult.success(body);
		}

		return body;
	}
}
