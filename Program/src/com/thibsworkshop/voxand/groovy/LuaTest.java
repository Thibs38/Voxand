package com.thibsworkshop.voxand.groovy;

import com.thibsworkshop.voxand.data.Biome;
import com.thibsworkshop.voxand.loaders.JsonLoader;
import com.thibsworkshop.voxand.toolbox.Utility;
import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class LuaTest {

    public static final String path = "Program/res/data/terrain_generation/";

    public void test(){
        //JsonLoader.init();


        /*GroovyShell shell = new GroovyShell();
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
        }*/


    }

    public static void main(String[] args) {
        new LuaTest().test();
    }

}
