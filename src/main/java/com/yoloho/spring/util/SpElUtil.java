package com.yoloho.spring.util;

import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * EL Expression parser
 * 
 * @author wuzl
 * 
 */
public class SpElUtil {
	private static final SpelExpressionParser EL_PARSER = new SpelExpressionParser();

	/**
	 * compute a expression without context
	 * 
	 * @param spEl
	 * @return
	 */
	public static final Object compute(String spEl) {
		return EL_PARSER.parseExpression(spEl).getValue();
	}

	/**
	 * compute a expression without context
	 * 
	 * @param spEl
	 * @return
	 */
	public static final Object compute(String spEl, boolean throwException) {
		try {
			return EL_PARSER.parseExpression(spEl).getValue();
		} catch (Exception e) {
			if (throwException) {
				throw new RuntimeException("spEL pasing error: " + e.getMessage());
			}
			return null;
		}
	}

}
