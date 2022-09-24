package com.li.common.js.rhino;

import org.mozilla.javascript.*;

import java.util.Map;

/**
 * 公式参数包装器工厂 - Map 包装器
 * @author Ramon
 */
@SuppressWarnings("rawtypes")
public class NativeWraperFactory extends WrapFactory {

	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		if (obj instanceof Map) {
			return NativeJavaMap.wrap((Map) obj, scope);
		}
		return super.wrap(cx, scope, obj, staticType);
	}

	@Override
	public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
		if (obj instanceof Map) {
			return NativeJavaMap.wrap((Map) obj, scope);
		}
		return super.wrapNewObject(cx, scope, obj);
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		if (obj instanceof Map) {
			return NativeJavaMap.wrap((Map) obj, scope);
		} else if(obj instanceof Number) {
			return NativeJavaNumber.wrap((Number) obj, scope);
		}
		return super.wrapAsJavaObject(cx, scope, obj, staticType);
	}

	@Override
	public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
		return super.wrapJavaClass(cx, scope, javaClass);
	}

}
