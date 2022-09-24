package com.li.common.js;

import com.li.common.js.rhino.Rhino;
import org.slf4j.*;

/**
 * 公式处理公共类 改为rhino实现
 * @author Ramon
 */
public class ExpressionHelper {

	static Logger log = LoggerFactory.getLogger(ExpressionHelper.class);

	/**
	 * 执行公式表达式
	 * @param expression 公式表达式
	 * @param resultType 执行结果类型
	 * @param ctx 公式执行上下文
	 * @return 公式表达式执行结果
	 */
	public static <T> T invoke(String expression, Class<T> resultType, Object ctx) {
		try {
			T result = Rhino.eval(expression, ctx, resultType);
			log.trace("公式内容[{}] 结果[{}]", expression, result);
			return result;
		} catch (RuntimeException e) {
			log.error("公式内容[{}] 执行错误", new Object[] { expression,  e });
			throw e;
		}
	}
}
