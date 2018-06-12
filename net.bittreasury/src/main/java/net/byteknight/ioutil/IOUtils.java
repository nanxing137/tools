/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package net.byteknight.ioutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class IOUtils {

	private IOUtils() {
		// no instances of this class
	}

	/**
	 * 从输入流中读取所有数据，并返回读取的字节。
	 */
	public static byte[] toByteArray(InputStream stream) throws IOException {
		return toByteArray(stream, Integer.MAX_VALUE);
	}

	/**
	 * 从流中读length长度byte数组
	 */
	public static byte[] toByteArray(InputStream stream, int length) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(length == Integer.MAX_VALUE ? 4096 : length);

		byte[] buffer = new byte[4096];
		int totalBytes = 0, readBytes;
		do {
			readBytes = stream.read(buffer, 0, Math.min(buffer.length, length - totalBytes));
			totalBytes += Math.max(readBytes, 0);
			if (readBytes > 0) {
				baos.write(buffer, 0, readBytes);
			}
		} while (totalBytes < length && readBytes > -1);

		if (length != Integer.MAX_VALUE && totalBytes < length) {
			throw new IOException("unexpected EOF");
		}

		return baos.toByteArray();
	}

	/**
	 * 返回length长度的byte数组
	 */
	public static byte[] toByteArray(ByteBuffer buffer, int length) {
		if (buffer.hasArray() && buffer.arrayOffset() == 0) {
			// The backing array should work out fine for us
			return buffer.array();
		}
		byte[] data = new byte[length];
		buffer.get(data);
		return data;
	}

	/**
	 * Helper method, just calls <tt>readFully(in, b, 0, b.length)</tt>
	 */
	public static int readFully(InputStream in, byte[] b) throws IOException {
		return readFully(in, b, 0, b.length);
	}

	/**
	 * <p>
	 * 如果没有读到任何数据，则返回-1</br>
	 * 如果读到了流的结尾，就返回读取的字节数</br>
	 * 如果在达到流结尾之前读取了len字节，就只读取到len字节，返回len
	 * </p>
	 * 
	 * @param in
	 *            从中读取数据的流。
	 * @param b
	 *            数据读入的缓冲区。
	 * @param off
	 *            写入数组起始偏移量 .
	 * @param len
	 *            要读取的最大字节数.
	 */
	public static int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
		int total = 0;
		while (true) {
			int got = in.read(b, off + total, len - total);
			if (got < 0) {
				return (total == 0) ? -1 : total;
			}
			total += got;
			if (total == len) {
				return total;
			}
		}
	}

	/**
	 * 将给定InputStream中的所有数据复制到OutputStream。 </br>
	 * 注:函数并没有关闭两个流
	 */
	public static void copy(InputStream inp, OutputStream out) throws IOException {
		byte[] buff = new byte[4096];
		int count;
		while ((count = inp.read(buff)) != -1) {
			if (count < -1) {
				throw new IllegalArgumentException("Can't have read < -1 bytes");
			}
			if (count > 0) {
				out.write(buff, 0, count);
			}
		}
	}

	/**
	 * Calculate checksum on input data
	 */
	public static long calculateChecksum(byte[] data) {
		Checksum sum = new CRC32();
		sum.update(data, 0, data.length);
		return sum.getValue();
	}

	/**
	 * 计算从输入流读取的所有数据的CRC32校验和。
	 */
	public static long calculateChecksum(InputStream stream) throws IOException {
		Checksum sum = new CRC32();

		byte[] buf = new byte[4096];
		int count;
		while ((count = stream.read(buf)) != -1) {
			if (count > 0) {
				sum.update(buf, 0, count);
			}
		}
		return sum.getValue();
	}
	
	public static String toString(InputStream inputStream) throws IOException {
		byte[] byteArray = toByteArray(inputStream);
		return new String(byteArray);
	}

}
