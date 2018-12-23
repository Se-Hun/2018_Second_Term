package fileIO;

import java.io.IOException;

public class BitInputManager {

	private ExtendedBufferedInputStream 	_bitInputStream;
	
	private int 	_bitBuffer;
	private int 	_numberOfBitsInBuffer;
	
	private ExtendedBufferedInputStream bitInputStream() {
		return this._bitInputStream;
	}
	private void setBitInputStream(ExtendedBufferedInputStream newBitOutputStrem) {
		this._bitInputStream = newBitOutputStrem;
	}

	private int bitBuffer() {
		return this._bitBuffer;
	}
	private void setBitBuffer(int newBitBuffer) {
		this._bitBuffer = newBitBuffer;
	}

	private int numberOfBitsInBuffer() {
		return this._numberOfBitsInBuffer;
	}
	private void setNumberOfbitsInBuffer(int newNumberOfBitsInBuffer) {
		this._numberOfBitsInBuffer = newNumberOfBitsInBuffer;
	}

	public BitInputManager(ExtendedBufferedInputStream givenBitOutputStream) {
		setBitInputStream(givenBitOutputStream);
		setNumberOfbitsInBuffer(0);
		setBitBuffer(0);
	}

	public int readBit() throws IOException {
		if(numberOfBitsInBuffer() == 0) {
			setBitBuffer(bitInputStream().read());
			setNumberOfbitsInBuffer(8);
		}
		setNumberOfbitsInBuffer(numberOfBitsInBuffer() - 1);
		switch(numberOfBitsInBuffer())
		{
		case 7:
			return (bitBuffer() & 0x80) == 0 ? 0 : 1;

		case 6:
			return (bitBuffer() & 0x40) == 0 ? 0 : 1;

		case 5:
			return (bitBuffer() & 0x20) == 0 ? 0 : 1;

		case 4:
			return (bitBuffer() & 0x10) == 0 ? 0 : 1;

		case 3:
			return (bitBuffer() & 8) == 0 ? 0 : 1;

		case 2:
			return (bitBuffer() & 4) == 0 ? 0 : 1;

		case 1:
			return (bitBuffer() & 2) == 0 ? 0 : 1;

		case 0:
			return (bitBuffer() & 1) == 0 ? 0 : 1;
		}
		return -1;
	}
}
