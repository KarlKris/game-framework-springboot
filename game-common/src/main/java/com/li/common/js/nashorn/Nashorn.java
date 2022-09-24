package com.li.common.js.nashorn;

import cn.hutool.core.convert.Convert;

import javax.script.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 * @date 2022/9/19
 */
public class Nashorn {

    private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("js");
    private final static Map<String, CompiledScript> COMPILED_SCRIPT = new ConcurrentHashMap<>();

    private final static ScriptContext SCRIPT_CONTEXT = ENGINE.getContext();
    private final static Bindings BINDINGS = ENGINE.createBindings();


    static {
        SCRIPT_CONTEXT.setBindings(BINDINGS, ScriptContext.GLOBAL_SCOPE);
    }

    public static <T> T eval(String expression, Object ctx, Class<T> type) throws ScriptException {
        BINDINGS.put("ctx", ctx);
        CompiledScript compiledScript = compile(expression);
        Object result = compiledScript.eval(SCRIPT_CONTEXT);
        return Convert.convert(type, result);
    }

    public static CompiledScript compile(String script) throws ScriptException {
        CompiledScript compiledScript = COMPILED_SCRIPT.get(script);
        if (compiledScript != null) {
            return compiledScript;
        }

        final Compilable compEngine = (Compilable) ENGINE;
        compiledScript = compEngine.compile(script);

        COMPILED_SCRIPT.put(script, compiledScript);
        return compiledScript;
    }

    public static final class ExpressionContext {

        private final Map<Integer, Long> attrs;

        public ExpressionContext() {
            this.attrs = new HashMap<>(100);
            for (int i = 1; i <= 100; i++) {
                this.attrs.put(i, i * 10L);
            }
        }

        public long getAttribute(int attr) {
            return attrs.getOrDefault(attr, 0L);
        }

    }


}
