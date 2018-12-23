package huffman;

public class HuffmanCode {

	private int _length;
	private boolean[] _code = new boolean[Huffman_CONST.MAX_CODE_LENGTH];
	
	private void setLength(int newLength) {
		this._length = newLength;
	}
	public int length() {
		return this._length;
	}
	
	private boolean[] code() {
		return this._code;
	}
	
	private void setBitAtIndex(int index, boolean bitValue) {
		this.code()[index] = bitValue;
	}
	
	public boolean bitAtIndex(int index) {
		if(index >= 0 && index < this.length()) {
			return this.code()[index];
		}
		else {
			throw new IllegalStateException("Illegal State Exception: Given Index is out of range");
		}
	}
	
	protected HuffmanCode() {
		this.reset();
	}
	
	protected HuffmanCode clone() {
		HuffmanCode clone = new HuffmanCode();
		clone.setLength(this.length());
		for(int i = 0; i < this.length(); i++) {
			clone.setBitAtIndex(i, this.bitAtIndex(i));
		}
		return clone;
	}
	
	protected void reset() {
		this.setLength(0);
	}
	
	protected void appendBit(boolean bitValue) {
		if(this.length() < Huffman_CONST.MAX_CODE_LENGTH) {
			this.setBitAtIndex(this.length(), bitValue);
			this.setLength(this.length() + 1);
		}
	}
	
	protected void removeLastBit() {
		if(this.length() > 0) {
			this.setLength(this.length() - 1);
		}
	}
}
