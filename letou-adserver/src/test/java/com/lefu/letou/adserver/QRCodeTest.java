package com.lefu.letou.adserver;

import io.netty.handler.codec.base64.Base64Encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.lefu.letou.common.FileUtil;

public class QRCodeTest {

	/**
	 * 生成图像
	 * 
	 * @throws WriterException
	 * @throws IOException
	 */
//	@Test
//	public void testEncode() throws WriterException, IOException {
//		// String filePath = "D:/";
//		// String fileName = "zxing.png";
//		long l1 = System.currentTimeMillis();
//		// for (int i = 0; i < 10; i++) {
//
//		String url = "https://github.com/zxing/zxing/tree/zxing-3.0.0/javase/src/main/java/com/google/zxing";
//		// json.put("author", "shihy");
//		// BitMatrix m = FileUtil.generateMatrix(json, 200, 200, "png");
//		BitMatrix m = FileUtil.generateQRCode(url, 200, 200, "png");
//		int[] bytes = new int[200 * 200];
//		byte[] b2 = new byte[200 * 200];
//		// BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(m);
//
//		for (int y = 0; y < 200; y++) {
//			for (int x = 0; x < 200; x++) {
//				if (m.get(x, y)) {
//					bytes[y * 200 + x] = 0xff000000;
//					b2[y * 200 + x] = (byte) 0xff000000;
//				} else {
//					bytes[y * 200 + x] = 0xffffffff;
//					b2[y * 200 + x] = 0xffffffff;
//				}
//			}
//		}
//
//		long l2 = System.currentTimeMillis();
//		System.out.println(l2 - l1);
//		// byte[] encode = java.util.Base64.getEncoder().encodeToString(b2);
//		String encode2 = new sun.misc.BASE64Encoder().encode(b2);
//		System.out.println(encode2.length());
//		// Path path = FileSystems.getDefault().getPath(filePath, fileName);
//		// MatrixToImageWriter.writeToPath(bitMatrix, format, path);// 输出图像
//
//		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
//		byte[] decodeBuffer = decoder.decodeBuffer(encode2);
//
//	}
//
//	@Test
//	public void testQR() throws WriterException, IOException {
//		String url = "http://www.baidu.com";
//		// BitMatrix m = FileUtil.generateMatrix(json, 200, 200, "png");
//		long l1 = System.currentTimeMillis();
//		BitMatrix m = FileUtil.generateQRCode(url, 200, 200, "png");
//
//		BufferedImage srcImage = MatrixToImageWriter.toBufferedImage(m,
//				new MatrixToImageConfig());
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		ImageIO.write(srcImage, "png", out);
//		byte[] data = out.toByteArray();
//		out.flush();
//		out.close();
//		String base64 = FileUtil.toBase64(data);
//		long l2 = System.currentTimeMillis();
//		System.out.println(l2 - l1);
//		System.out.println(base64);
//	}
//
//	@Test
//	public void reversBase64() throws IOException {
//		String base64 = "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";
//		writoFile(base64);
//	}
//
//	public static void main(String[] args) throws IOException {
//		FileInputStream is = new FileInputStream("d:/zxing.png");
//		byte[] b = new byte[1024];
//		is.read(b);
//		is.close();
//		String encode2 = new sun.misc.BASE64Encoder().encode(b);
//		System.out.println(encode2);
//		writoFile(encode2);
//	}
//
//	private static void writoFile(String base64) throws IOException {
//		org.apache.commons.codec.binary.Base64 base = new Base64();
//		byte[] decodeBuffer  = base.decode(base64);
//		String s= new String(decodeBuffer);
//		FileOutputStream os = new FileOutputStream("d:/new.jpg");
//		os.write(decodeBuffer);
//		os.flush();
//		os.close();
//	}

	/**
	 * 解析图像
	 */
	// @Test
	// public void testDecode() {
	// String filePath = "D:/zxing.png";
	// BufferedImage image;
	// try {
	// image = ImageIO.read(new File(filePath));
	// LuminanceSource source = new BufferedImageLuminanceSource(image);
	// Binarizer binarizer = new HybridBinarizer(source);
	// BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
	// Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType,
	// Object>();
	// hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
	// Result result = new MultiFormatReader().decode(binaryBitmap, hints);//
	// 对图像进行解码
	// JSONObject content = JSONObject.parseObject(result.getText());
	// System.out.println("图片中内容：  ");
	// System.out.println("author： " + content.getString("author"));
	// System.out.println("zxing：  " + content.getString("zxing"));
	// System.out.println("图片中格式：  ");
	// System.out.println("encode： " + result.getBarcodeFormat());
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (NotFoundException e) {
	// e.printStackTrace();
	// }
	// }
}