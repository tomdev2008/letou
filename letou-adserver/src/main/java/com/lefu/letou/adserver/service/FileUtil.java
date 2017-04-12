package com.lefu.letou.adserver.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class FileUtil {
	public static final Logger posLogger = Logger.getLogger("poslog");
	public static final Logger posErrorLog = Logger.getLogger("posErrorLog");
	public static final Logger appLogger = Logger.getLogger("applog");
	public static final Logger appErrorLog = Logger.getLogger("appErrorLog");

	private static final QRCodeWriter qrCodeWriter = new QRCodeWriter();
	private static final Base64 base64 = new Base64();

	public static Properties loadProps(String file) throws IOException {
		InputStream stream = FileUtil.class.getResourceAsStream(file);
		Properties p = null;
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(stream));
			p = new Properties();
			p.load(r);
		} finally {
			stream.close();
		}
		return p;
	}

	// matrix转成图像, 写到字节数组
//	BufferedImage srcImage = MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig())
//	ByteArrayOutputStream out = new ByteArrayOutputStream();
//	ImageIO.write(srcImage, "jpg", out);
//	byte[] data = out.toByteArray();
	
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

	public static BitMatrix generateQRCode(String url, int width, int height,
			String format) throws WriterException {
		if (width == 0) {
			width = 200; // 图像宽度
		}
		if (height == 0) {
			height = 200; // 图像高度
		}
		if (format == null || format.equals("")) {
			format = "bmp";// 图像类型
		}
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		return qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
	}

	public static String toBase64(byte[] bytes) {
//		String s1 = e1(bytes);
		String s2 = e2(bytes);

		long l1 = System.currentTimeMillis();
		base64.encodeToString(bytes);
		long l2 = System.currentTimeMillis();
		System.out.println(l2 - l1);
		return base64.encodeToString(bytes);
	}

//	private static String e1(byte[] bytes) {
//		long l1 = System.currentTimeMillis();
//		String s1 = java.util.Base64.getEncoder().encodeToString(bytes);
//		long l2 = System.currentTimeMillis();
//		System.out.println(l2 - l1);
//		return s1;
//	}

	private static String e2(byte[] bytes) {
		long l1 = System.currentTimeMillis();
		String s2 = new sun.misc.BASE64Encoder().encode(bytes);
		long l2 = System.currentTimeMillis();
		System.out.println(l2 - l1);
		return s2;
	}

}
