/**
 * 
 */
package test;

/**
 * @author guozy
 * @date 2016-9-20
 * 
 */

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 进制之间的转换
 * 
 * @author jwzhangjie
 *
 */
public class HexadecimalConver {

	private static String hexString = "0123456789ABCDEF";

	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "GB2312");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/**
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String bytes) throws UnsupportedEncodingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2) {
			// int c3 = hexString.indexOf(bytes.charAt(i));
			// int c1 = hexString.indexOf(bytes.charAt(i)) << 4;
			// int c2 = hexString.indexOf(bytes.charAt(i + 1));
			// System.out.println(c3);
			// System.out.println(c1);
			// System.out.println(c2);
			// System.out.println(c1 | c2);
			// baos.write(188);
			// baos.write(66);
			// String a = new String(baos.toByteArray(), "big5");
			// System.out.println(a);
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
		}

		String bb = "";

		bb = new String(baos.toByteArray(), "big5");

		return bb;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(decode("BC42B3C6"));
	}
}
