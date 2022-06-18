package com.thibsworkshop.voxand.lua;

import com.thibsworkshop.voxand.data.IBiome;
import com.thibsworkshop.voxand.loaders.LuaLoader;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class LuaTest {

    public static final String path = "Program/res/data/terrain_generation/";

    public void test(){
        /*Globals globals = JsePlatform.standardGlobals();
        LuaValue test = CoerceJavaToLua.coerce(new TestClass());
        globals.set("obj", test);
        LuaTable t = new LuaTable();
        t.set("test", new TestClass.Test());
        t.set("__index", t);
        test.setmetatable(t);
        LuaValue chunk = globals.load("obj.test()");
        chunk.call();*/

        /*Globals globals = JsePlatform.standardGlobals();

        // Load a class into globals, 'field' cannot be accessed
        LuaValue cls = CoerceJavaToLua.coerce(MyClass.class);
        globals.set("cls", cls);
        LuaValue chunk = globals.loadfile(path + "plains.lua");
        LuaLoader.init();
        LuaLoader.loadBiomes();
        chunk.call();*/

        GroovyShell shell = new GroovyShell();
        try {
            final Object e = shell.evaluate(new File(path + "test.groovy"));
            IBiome biome = (IBiome) Proxy.newProxyInstance(e.getClass().getClassLoader(),
                    new Class[]{IBiome.class},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args)
                                throws Throwable {
                            Method m = e.getClass().getMethod(method.getName());
                            return m.invoke(e, args);
                        }});
            //System.out.println(biome.generate_xz(1, 2));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }


    public static class MyClass {
        public static String variable = "variable-value";
        public static MyOtherClass other = new MyOtherClass(5);
        public static String func() {
            return "function-result";
        }

    }

    public static class MyOtherClass{
        public int id;
        public MyOtherClass(int id){
            this.id = id;
        }
    }

    public static void main(String[] args) {
        new LuaTest().test();
    }

}
