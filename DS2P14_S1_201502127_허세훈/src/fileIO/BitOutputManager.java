package fileIO;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class BitOutputManager {

	private BufferedOutputStream 	_bitOutputStream;
	
	private int 	_bitBuffer;
	private int 	_numberOfBitsInBuffer;
	
	private BufferedOutputStream bitOutputStream() {
		return this._bitOutputStream;
	}
	private void setBitOutputStream(BufferedOutputStream newBitOutputStream) {
		this._bitOutputStream = newBitOutputStream;
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
	private void setNumberOfBitsInBuffer(int newNumberOfBitsInBuffer) {
		this._numberOfBitsInBuffer = newNumberOfBitsInBuffer;
	}
	
	public BitOutputManager(BufferedOutputStream givenBitOutputStream) {
		this.setBitOutputStream(givenBitOutputStream);
		this.setNumberOfBitsInBuffer(0);
		this.setBitBuffer(0);
	}
	
	public void writeBit(boolean bitValue) throws IOException {
		this.setBitBuffer(this.bitBuffer() << 1);
		if(bitValue) {
			this.setBitBuffer(this.bitBuffer() + 1);
		}
		this.setNumberOfBitsInBuffer(this.numberOfBitsInBuffer() + 1);
		
		if(this.numberOfBitsInBuffer() == 8) {
			this.bitOutputStream().write(this.bitBuffer());
			this.setBitBuffer(0);
			this.setNumberOfBitsInBuffer(0);
		}
	}
	
	public void flush() throws IOException {
		if(this.numberOfBitsInBuffer() > 0 &&
				this.numberOfBitsInBuffer() < FileIO_CONST.NUMBER_OF_BITS_OF_BYTE)
		{
			int numberOfShiftLeftsToMakeLeftJustifiedInByte = 
					FileIO_CONST.NUMBER_OF_BITS_OF_BYTE - this.numberOfBitsInBuffer();
			this.setBitBuffer(this.bitBuffer() << numberOfShiftLeftsToMakeLeftJustifiedInByte);
			this.bitOutputStream().write(this.bitBuffer());
			this.setBitBuffer(0);
			this.setNumberOfBitsInBuffer(0);
		}
	}
}
