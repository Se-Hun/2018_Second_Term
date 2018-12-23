package huffman;

public class HuffmanTreeNode {

	private HuffmanTreeNode _left;
	private HuffmanTreeNode _right;
	
	protected HuffmanTreeNode left() {
		return this._left;
	}
	protected void setLeft(HuffmanTreeNode newLeft) {
		this._left = newLeft;
	}
	
	protected HuffmanTreeNode right() {
		return this._right;
	}
	protected void setRight(HuffmanTreeNode newRight) {
		this._right = newRight;
	}
	
	protected HuffmanTreeNode() {
		
	}
	protected HuffmanTreeNode(HuffmanTreeNode givenLeft, HuffmanTreeNode givenRight) {
		this.setLeft(givenLeft);
		this.setRight(givenRight);
	}
	
	protected boolean isLeaf() {
		return (this.left() == null && this.right() == null);
	}
}
