package huffman;

public class HuffmanDecoder {

	private short[][] 	_serializedHuffmanTree;
	private int 		_currentNodeIndex;
	
	private short[][] serializedHuffmanTree() {
		return this._serializedHuffmanTree;
	}
	private void setSerializedHuffmanTree(short newSerializedHuffmanTree[][]) {
		this._serializedHuffmanTree = newSerializedHuffmanTree;
	}

	private int currentNodeIndex() {
		return this._currentNodeIndex;
	}

	private void setCurrentNodeIndex(int newCurrentNodeIndex) {
		this._currentNodeIndex = newCurrentNodeIndex;
	}

	public HuffmanDecoder(short givenSerializedHuffmanTree[][]) {
		setSerializedHuffmanTree(givenSerializedHuffmanTree);
		setCurrentNodeIndex(0);
	}

	public int decodeBit(int bitValue) {
		if(bitValue == 0) {
			setCurrentNodeIndex(serializedHuffmanTree()[currentNodeIndex()][0]);
		} 
		else {
			if(bitValue == 1) {
				setCurrentNodeIndex(serializedHuffmanTree()[currentNodeIndex()][1]);
			}
			else {
				
			}
		}
		
		if(serializedHuffmanTree()[currentNodeIndex()][0] != -1) {
			return -1;
		} 
		else {
			int decodedBit = serializedHuffmanTree()[currentNodeIndex()][1];
			setCurrentNodeIndex(0);
			return decodedBit;
		}
	}
}
