package iptest;

public class Test {

	// 1.2.3.4 = 1 * 256 * 256 * 256 + 2 * 256 * 256 + 3 * 256 + 4

	public static void main(String[] args) {
		String s = int2ip(17563648);
		System.out.println(s);
	}

	public static String int2ip(long ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}
}
