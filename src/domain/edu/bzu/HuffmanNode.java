package domain.edu.bzu;

public class HuffmanNode{

	public HuffmanNode left,right;
	public long freq;
	public char ch;
	public String huffCode;


	public HuffmanNode(){
		freq = 0;
		ch = 0;
		huffCode = "";
		left = null;
		right = null;
		}
	
	
	public HuffmanNode(char ch) {
		super();
		this.ch = ch;
	}


	public HuffmanNode(char ch, long freq){
		this.freq = freq;
		this.ch = ch;
		this.left = null;
		this.right = null;
		this.huffCode = "";
		}
	public HuffmanNode(char ch, long freq , String huffCode,HuffmanNode left, HuffmanNode right) {
		super();
		this.left = left;
		this.right = right;
		this.freq = freq;
		this.ch = ch;
		this.huffCode = huffCode;
	}
	public HuffmanNode getLeft() {
		return left;
	}
	public void setLeft(HuffmanNode left) {
		this.left = left;
	}
	public HuffmanNode getRight() {
		return right;
	}
	public void setRight(HuffmanNode right) {
		this.right = right;
	}
	public long getFreq() {
		return freq;
	}
	public void setFreq(long freq) {
		this.freq = freq;
	}
	public char getCh() {
		return ch;
	}
	public void setCh(char ch) {
		this.ch = ch;
	}
	public String getHuffCode() {
		return huffCode;
	}
	public void setHuffCode(String huffCode) {
		this.huffCode = huffCode;
	}
	
	
	
	


}