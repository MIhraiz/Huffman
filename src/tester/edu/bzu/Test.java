package tester.edu.bzu;

import domain.edu.bzu.MyConverter;

public class Test {

	public static void main(String[] args) {
		/*
		String temp = "770";
		System.out.println((byte)((char) 255));
		System.out.println(temp.length() +" "+ temp.charAt(0));
		System.out.println((char)0);
		byte b = -105;
		char c = (char) b;
		System.out.println(c);
		c = (char) (b + 128);
		System.out.println(c);
		c = 'م';
		System.out.println(c);
		
		byte b2 = -1;
		byte b3 = 0;
		
		System.out.println(MyConverter.byteToBinary(b3));
		System.out.println(MyConverter.byteToBinary(b2));
		
		byte num = -1;
		short n = (short) (num+256);
		System.out.println(n);
		int space = 0;
		for(int i = 9; i > 0; i--) {
			for(int j = i; j > 0; j--) {
				System.out.print(i);
			}
			for(int j = space; j > 0; j--) {
				System.out.print(" ");
			}
			space += 2;
			for(int j = i; j > 0; j--) {
				System.out.print(i);
			}
			System.out.println();
		}
		
		*/
		
		MyConverter conv = new MyConverter();
		String s = conv.IntegerToBinary(178975).replace(" ", "0");
		System.out.println(s);
		
		System.out.println(Byte.parseByte("11111111",2));
		
		String size = MyConverter.IntegerToBinary(56509824).replace(" ", "0");
	    System.out.println(size);
	    byte x = 0, y = 8;
	    for(int i = 0; i <4 ; i++) {
	
	    		System.out.println(size.substring(x,y));
		    	x+=8;
		    	y+=8;
	    	
	    }
	}

}
