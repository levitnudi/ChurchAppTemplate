package us.shandian.giga.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class FileUtils {

	public static void close(Reader reader) {
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
			}
	}

	public static void close(Writer writer) {
		if (writer != null)
			try {
				writer.close();
			} catch (IOException e) {
			}
	}

	public static void close(InputStream inputStream) {
		if (inputStream != null)
			try {
				inputStream.close();
			} catch (IOException e) {
			}
	}

	public static void close(OutputStream outputStream) {
		if (outputStream != null)
			try {
				outputStream.close();
			} catch (IOException e) {
			}
	}
}
