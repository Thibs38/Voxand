package com.thibsworkshop.voxand.rendering.gui;


import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.rendering.textures.Texture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads the file format specified here: http://www.angelcode.com/products/bmfont/doc/file_format.html
 */
public class FontLoader {

    private int countChars;
    private int lineHeight;

    private int textureWidth, textureHeight;

    public BitmapFont load(File file) throws IOException {
        countChars = 0;
        textureWidth = 0;
        textureHeight = 0;
        BitmapFont bitmapFont = new BitmapFont();

        BufferedReader b = new BufferedReader(new FileReader(file));

        String readLine = "";
        while ((readLine = b.readLine()) != null) {
            String[] tokens = readLine.trim().split("\\s+");

            if (tokens[0].equalsIgnoreCase("info")) {
                continue;
            }
            if (tokens[0].equalsIgnoreCase("kernings")) {
                continue;
            }
            if (tokens[0].equalsIgnoreCase("kerning")) {
                continue;
            }

            if (tokens[0].equalsIgnoreCase("common")) {
                lineHeight = Integer.parseInt(getValue(tokens[1]));
                bitmapFont.setLineHeight(lineHeight);

                textureWidth = Integer.parseInt(getValue(tokens[3]));
                textureHeight = Integer.parseInt(getValue(tokens[4]));
            }
            // Load the .tga texture
            if (tokens[0].equalsIgnoreCase("page")) {
                int id = Integer.parseInt(getValue(tokens[1]));
                Texture page = Loader.loadTexture(file.getParent()+"/"+getValue(tokens[2]).replace("\"", ""));
                bitmapFont.addPage(id, page);
            }
            if (tokens[0].equalsIgnoreCase("chars")) {
                countChars = Integer.parseInt(getValue(tokens[1]));
            }
            if (tokens[0].equalsIgnoreCase("char")) {
                int id = Integer.parseInt(getValue(tokens[1]));
                float x = (float) Integer.parseInt(getValue(tokens[2])) / textureWidth;
                float y = (float) Integer.parseInt(getValue(tokens[3])) / textureHeight;
                float width = (float) Integer.parseInt(getValue(tokens[4])) / textureWidth;
                float height = (float) Integer.parseInt(getValue(tokens[5])) / textureHeight;
                int xoffset = Integer.parseInt(getValue(tokens[6]));
                int yoffset = Integer.parseInt(getValue(tokens[7]));
                int xadvance = Integer.parseInt(getValue(tokens[8]));
                int page = Integer.parseInt(getValue(tokens[9]));
                //int chnl = Integer.parseInt(getValue(tokens[10]));

                BitmapFont.Glyph g = bitmapFont.new Glyph(id, x, y, width, height, xoffset, yoffset, xadvance, page);
                bitmapFont.addGlyph(g);
            }
        }
        return bitmapFont;
    }

    /**
     * Takes a token with the format "foo=bar" and returns the right side of the equals-operator.
     */
    private String getValue(String token) {
        return token.split("=")[1];
    }
}

