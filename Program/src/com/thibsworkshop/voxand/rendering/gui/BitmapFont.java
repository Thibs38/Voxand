package com.thibsworkshop.voxand.rendering.gui;

import com.thibsworkshop.voxand.rendering.textures.Texture;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes a bitmap font.
 */
public class BitmapFont {

    public class Glyph {

        int id; // character id (unicode decimal value)
        float x; // left position of the character image in the texture
        float y; // top position of the character image in the texture

        int width; // width of the character image in the texture
        int height; // height of the character image in the texture

        int xoffset; // How much the current position should be offset when copying the image from the texture to the screen
        int yoffset; // How much the current position should be offset when copying the image from the texture to the screen.

        int xadvance; // How much the current position should be advanced after drawing the character.
        int page; // The texture page where the character image is found.

        public Glyph(int id, float x, float y, float width, int height, int xoffset, int yoffset, int xadvance, int page) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
            this.xadvance = xadvance;
            this.page = page;
        }

        public String toString() {
            return "Glyph: "+id+" "+x+" "+y+" "+width+" "+height+" "+xoffset+" "+yoffset+" "+xadvance+" "+page;
        }
    }

    private int lineHeight = 15; // how many pixels to move the cursor down to find new line

    // glyphs mapped by id (unicode decimal values)
    private Map<Integer, Glyph> glyphs = new HashMap<>();

    // pages mapped by their id
    private Map<Integer, Texture> pages = new HashMap<>();

    public void addGlyph(Glyph glyph) {
        glyphs.put(glyph.id, glyph);
    }

    public void addPage(int id, Texture page) {
        pages.put(id, page);
    }

    public Texture getPage(int id) {
        return pages.get(id);
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    /**
     * Calculates the size of the GUIText for a given character sequence
     * @param data the character sequence
     * @param dest the destination size Vector
     * @return the size Vector
     */
    public Vector2f getBounds(CharSequence data, Vector2f dest) {
        int xadvance = 0;
        int yadvance = 0;
        int highestX = 0;
        for (int i = 0; i < data.length(); i++) {
            int code = Character.codePointAt(data, i);

            if (code == 10) { // linefeed encountered
                yadvance += lineHeight;
                xadvance = 0;
                continue;
            }

            Glyph g = glyphs.get(code);
            if (g == null) {
                continue;
            }
            xadvance += g.xadvance;
            if (xadvance > highestX) {
                highestX = xadvance;
            }
        }
        dest.set(highestX, yadvance);
        return dest;
    }

    /**
     * Draws the string one character at a time.
     * @param batch     batch used to draw the string
     * @param data      string to draw
     * @param x         top-left corner of text
     * @param y
     */
    public void drawString(TextureBatch batch, CharSequence data, int x, int y) {
        int xadvance = 0;
        int yadvance = 0;
        for (int i = 0; i < data.length(); i++) {
            int code = Character.codePointAt(data, i);

            if (code == 10) { // linefeed encountered
                yadvance += lineHeight;
                xadvance = 0;
                continue;
            }

            Glyph g = glyphs.get(code);
            if (g == null) {
                continue;
            }

            Texture page = pages.get(g.page);

            float glyphWidth = g.width*page.getWidth();
            float glyphHeight = g.height*page.getHeight();

            batch.drawTexture(page, x+g.xoffset+xadvance, y-glyphHeight-g.yoffset-yadvance, glyphWidth, glyphHeight, g.x, g.y, g.width, g.height);
            int positionX = x + g.xoffset + xadvance;
            int positionY = y - glyphHeight - g.yoffset - yadvance;

            xadvance += g.xadvance;
        }
    }

}
