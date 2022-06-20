package com.thibsworkshop.voxand.debugging;

public class CompilingException {

    public static class IllegalJSONArrayType extends Exception {
        public IllegalJSONArrayType() { super(); }
        public IllegalJSONArrayType(String message) { super(message); }
        public IllegalJSONArrayType(String message, Throwable cause) { super(message, cause); }
        public IllegalJSONArrayType(Throwable cause) { super(cause); }
    }

    public static class IllegalJSONObjectType extends Exception {
        public IllegalJSONObjectType() { super(); }
        public IllegalJSONObjectType(String message) { super(message); }
        public IllegalJSONObjectType(String message, Throwable cause) { super(message, cause); }
        public IllegalJSONObjectType(Throwable cause) { super(cause); }
    }
}
