package com.thibsworkshop.voxand.loaders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.thibsworkshop.voxand.rendering.textures.Texture;
import de.matthiasmann.twl.utils.PNGDecoder;

import org.lwjgl.opengl.GL11;

public class TextureLoader {

	public static Texture loadTexture(String extension, String fileName){

	    //load png file
	    PNGDecoder decoder = null;
	    InputStream stream = null;
		try {
			stream = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {
			System.err.println("Couldn't load texture: " + fileName);
			e1.printStackTrace();
			return null;
		}
		
		try {
			decoder = new PNGDecoder(stream);
			
		} catch (IOException e) {
			System.err.println("Couldn't load texture: " + fileName);
			e.printStackTrace();
			return null;
		}

	    //create a byte buffer big enough to store RGBA values
	    ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

	    //decode
	    try {
			decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
		} catch (IOException e) {
			e.printStackTrace();
		}

	    //flip the buffer so its ready to read
	    buffer.flip();

	    //create a texture
	    int id = GL11.glGenTextures();

	    //bind the texture
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

	    //tell opengl how to unpack bytes
	    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

	    //set the texture parameters, can be GL_LINEAR or GL_NEAREST
	    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

	    //upload texture
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

	    // Generate Mip Map
	    //GL11.glGenerateMipmap(GL11.GL_TEXTURE_2D);

	    return new Texture(id); 
	}
}
