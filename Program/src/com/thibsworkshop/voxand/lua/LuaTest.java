package com.thibsworkshop.voxand.lua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;


public class LuaTest {

    public static final String path = "Program/res/lua/";

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

        Globals globals = JsePlatform.standardGlobals();

        // Load a class into globals, 'field' cannot be accessed
        LuaValue cls = CoerceJavaToLua.coerce(MyClass.class);
        globals.set("cls", cls);
        LuaValue chunk = globals.loadfile(path + "example.lua");

        chunk.call();
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
