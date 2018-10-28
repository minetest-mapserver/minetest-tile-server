package io.rudin.minetest.tileserver.util;

import org.luaj.vm2.LuaTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

public class LuaParser {

    private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("luaj");

    private static final Logger logger = LoggerFactory.getLogger(LuaParser.class);

    public static LuaTable parseMap(String str) {
        try {
            Object result = engine.eval(str);

            if (result instanceof LuaTable) {
                return (LuaTable) result;
            }

            return null;

        } catch (ScriptException e){
            throw new IllegalArgumentException("parseMap", e);
        }
    }

}
