package com.thibsworkshop.voxand.toolbox;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utility {

	/**
	 * Read the file specified with path with the specified encoding and returns the content in a string
	 * @param path the path of the file
	 * @param encoding the encoding to read the file with
	 * @return the content of the file
	 * @throws IOException can't read the specified file
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
	}

}
