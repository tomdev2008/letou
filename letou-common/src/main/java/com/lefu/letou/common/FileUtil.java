package com.lefu.letou.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FileUtil {
	public static Properties loadProps(String file) throws IOException {
		InputStream stream = FileUtil.class.getResourceAsStream(file);
		Properties p = null;
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(stream));
			p = new Properties();
			p.load(r);
		} finally {
			stream.close();
			r.close();
		}
		return p;
	}

	// matrix转成图像, 写到字节数组
	// BufferedImage srcImage = MatrixToImageWriter.toBufferedImage(bitMatrix,
	// new MatrixToImageConfig())
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// ImageIO.write(srcImage, "jpg", out);
	// byte[] data = out.toByteArray();

	// public static BitMatrix generateMatrix(JSONObject json, int width,
	// int height, String format) throws WriterException {
	// String content = json.toJSONString();// 内容
	// if (width == 0) {
	// width = 200; // 图像宽度
	// }
	// if (height == 0) {
	// height = 200; // 图像高度
	// }
	// if (format == null || format.equals("")) {
	// format = "bmp";// 图像类型
	// }
	// Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType,
	// Object>();
	// hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	// BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
	// BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
	// return bitMatrix;
	// }

//	public static BitMatrix generateQRCode(String url, int width, int height,
//			String format) throws WriterException {
//		if (width == 0) {
//			width = 200; // 图像宽度
//		}
//		if (height == 0) {
//			height = 200; // 图像高度
//		}
//		if (format == null || format.equals("")) {
//			format = "bmp";// 图像类型
//		}
//		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
//		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//		return qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
//	}
//
//	public static String generateBase64(String url, int width, int height,
//			String format) throws WriterException, IOException {
//		BitMatrix bm = generateQRCode(url, width, height, format);
//
//		BufferedImage srcImage = MatrixToImageWriter.toBufferedImage(bm,
//				new MatrixToImageConfig());
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		ImageIO.write(srcImage, format, out);
//		out.flush();
//		out.close();
//		byte[] data = out.toByteArray();
//		return toBase64(data);
//	}
//
//	public static String toBase64(byte[] bytes) {
//		return base64.encodeToString(bytes);
//	}
	//
	// private static String e1(byte[] bytes) {
	// long l1 = System.currentTimeMillis();
	// String s1 = java.util.Base64.getEncoder().encodeToString(bytes);
	// long l2 = System.currentTimeMillis();
	// System.out.println(l2 - l1);
	// return s1;
	// }
	//
	// private static String e2(byte[] bytes) {
	// long l1 = System.currentTimeMillis();
	// String s2 = new sun.misc.BASE64Encoder().encode(bytes);
	// long l2 = System.currentTimeMillis();
	// System.out.println(l2 - l1);
	// return s2;
	// }

}
