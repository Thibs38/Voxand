package com.thibsworkshop.voxand.rendering.gui;

public class GUIText {

    String text;
    int fontHeight;
    int lineHeight;
    int lineCount;

    Truetype font;

    public GUIText(String text, int height, Truetype font){
        this.fontHeight = height;
        this.lineHeight = fontHeight;
        this.font = font;
    }

    /* Read text from file *//*
    public GUIText(int height, Truetype font){
        this.fontHeight = height;
        this.lineHeight = fontHeight;
        this.font = font;
        String t;

        int lc;

        try {
            ByteBuffer source = Utility.ioResourceToByteBuffer("", 4 * 1024);
            t = MemoryUtil.memUTF8(source).replaceAll("\t", "    "); // Replace tabs

            lc = 0;
            Matcher m = Pattern.compile("^.*$", Pattern.MULTILINE).matcher(t);
            while (m.find()) {
                lc++;
            }
        } catch (IOException e) {
            e.printStackTrace();

            t = "Failed to load text.";
            lc = 1;
        }

        text = t;
        lineCount = lc;
    }*/
}
