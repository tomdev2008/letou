package iptest;

public class TestIP {
	public static void main(String[] args) {
		IP.enableFileWatch = true; // 默认值为：false，如果为true将会检查ip库文件的变化自动reload数据
		IP.load("/iptest/17monipdb.dat");
		String[] find = IP.find("211.100.254.114"); // 返回字符串数组["GOOGLE","GOOGLE"]
		
		for (String string : find) {
			System.out.println(string);
		}
	}
}
