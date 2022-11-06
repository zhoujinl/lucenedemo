package lucenedemo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 项目配置信息的工具类
 *
 * @author zrx
 */
@Getter
@Component("config")
public class Config {

	@Value("${lucene.indexLibrary}")
	private String indexLibrary;

}
