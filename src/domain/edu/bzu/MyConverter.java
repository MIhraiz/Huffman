package domain.edu.bzu;

public class MyConverter {
	
	
	public static String byteToBinary(byte b) {
		if(b < 0) {
			return String.format("%8s", Integer.toBinaryString(b+256)).replace(" ", "0");
		}
		return String.format("%8s", Integer.toBinaryString(b)).replace(" ", "0");
		
	}
	
	public static String IntegerToBinary(int n) {
		String s = String.format("%32s", Integer.toBinaryString(n));
		return s;
	}
	
	
	public static String shortToBinary(short sh) {
		String s = String.format("%16s", Integer.toBinaryString(sh));
		return s;
	}
	
	public static byte binaryToByte(String binary) {
		int n = Integer.parseInt(binary,2);
		
		if(n > 127) {
			n = n -256;
			return (byte) n ;
		}
		return Byte.parseByte(binary, 2);
	}
	

}
