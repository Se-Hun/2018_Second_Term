package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fileIO.BitInputManager;
import fileIO.ExtendedBufferedInputStream;
import fileIO.ExtendedBufferedOutputStream;
import fileIO.FilePathManager;
import huffman.HuffmanDecoder;

public class DecompressionController {

	private File 				_compressedFile;
	private File 				_decompressedFile;
	private ExtendedBufferedInputStream _compressedInputStream;
	private ExtendedBufferedOutputStream _decompressedOutputStream;
	private BitInputManager _bitInputManager;
	private HuffmanDecoder 	_huffmanDecoder;
	
	private File compressedFile() {
		return this._compressedFile;
	}
	private void setCompressedFile(File newCompressedFile) {
		this._compressedFile = newCompressedFile;
	}
	
	private File decompressedFile() {
		return this._decompressedFile;
	}
	private void setDecompressedFile(File newDecompressedFile) {
		this._decompressedFile = newDecompressedFile;
	}
	
	private ExtendedBufferedInputStream compressedInputStream() {
		return this._compressedInputStream;
	}
	private void setCompressedInputStream(ExtendedBufferedInputStream newCompressedInputStream) {
		this._compressedInputStream = newCompressedInputStream;
	}
	
	private ExtendedBufferedOutputStream decompressedOutputStream() {
		return this._decompressedOutputStream;
	}
	private void setDecompressedOutputStream(ExtendedBufferedOutputStream newDecompressedOutputStream) {
		this._decompressedOutputStream = newDecompressedOutputStream;
	}
	
	private BitInputManager bitInputManager() {
		return this._bitInputManager;
	}
	private void setBitInputManager(BitInputManager newBitInputManager) {
		this._bitInputManager = newBitInputManager;
	}
	
	private HuffmanDecoder huffmanDecoder() {
		return this._huffmanDecoder;
	}
	private void setHuffmanDecoder(HuffmanDecoder newHuffmanDecoder) {
		this._huffmanDecoder = newHuffmanDecoder;
	}
	
	private boolean initCompressedFile() {
		
		AppView.outputLine("");
		AppView.outputLine("? ������ Ǯ ������ ��ο� �̸��� �Է��Ͻÿ�: ");
		
		String filePath = AppView.inputFilePath();
		String fileName = AppView.inputFileName();
		String filePathAndName = (new StringBuilder(String.valueOf(filePath))).append("/").append(fileName).toString();
		
		setCompressedFile(new File(filePathAndName));
		
		if(compressedFile().exists()) {
			return true;
        } else {
        	AppView.outputLine((new StringBuilder("!����: ���� ���� (")).append(filePathAndName).append(") �� �������� �ʽ��ϴ�.").toString());
        	return false;
        }
	}
	
	private void initDecompressedFile() {
		
		AppView.outputLine("");
		String filePathAndName = FilePathManager.getFilePathAndNameWithoutExtension(compressedFile());
		setDecompressedFile(new File(filePathAndName));
		
		if(decompressedFile().exists()) {
			AppView.outputLine((new StringBuilder("!���: ���� ���� ���� (")).append(filePathAndName).append(") �� �̹� �����մϴ�.").toString());
			AppView.outputLine("- ���� ���� ������ �̸��� �ٸ� ������ �ٲپ� ó���մϴ�:");
			
			String decompressedFilePathAndnameWithoutExtension = 
					FilePathManager.getFilePathAndNameWithoutExtension(decompressedFile());
			String decompressedFilePathAndNameWithInfix = 
					(new StringBuilder(String.valueOf(decompressedFilePathAndnameWithoutExtension))).append("_UNZIP_").toString();
			String decompressedFilePathAndName = null;
			
			int decompressedFileSerialNumber = 0;
			
			do {
				decompressedFileSerialNumber++;
				decompressedFilePathAndName = 
						(new StringBuilder(String.valueOf(decompressedFilePathAndNameWithInfix))).append(decompressedFileSerialNumber).append(".txt").toString();
				setDecompressedFile(new File(decompressedFilePathAndName));
			} while(decompressedFile().exists());
			
			AppView.outputLine((new StringBuilder("- ���ο� ���� ���� ������ �̸��� ")).append(decompressedFilePathAndName).append(" �Դϴ�.").toString());
		}
	}
	
	private short[][] readSerializedHuffmanTree() throws IOException {
		
		int numberOfNodesInSerializedHuffmanTree = compressedInputStream().readShort();
		short serializedHuffmanTree[][] = new short[numberOfNodesInSerializedHuffmanTree][2];
		
		for(int i = 0; i < numberOfNodesInSerializedHuffmanTree; i++) {
			try {
				serializedHuffmanTree[i][0] = compressedInputStream().readShort();
				serializedHuffmanTree[i][1] = compressedInputStream().readShort();
			} catch(IOException e) {
				AppView.outputLine("!����: [����ȭ�� ������ Ʈ��]�� �д� ���� �����߽��ϴ�.");
				throw e;
			}
		}
		
		return serializedHuffmanTree;
	}
	
	private long readNumberOfBitsOfCompressedData() throws IOException {
		try {
			return compressedInputStream().readLong();
		} catch(IOException e) {
			AppView.outputLine("!����: [����� �������� ��Ʈ ��]�� ���Ͽ��� �о� ���� ���� �����߽��ϴ�.");
			throw e;
		}
	}
	
	private void openDecompressedOutputStream() throws IOException {
		try {
			FileOutputStream decompressedFileOutputStream = new FileOutputStream(decompressedFile());
			setDecompressedOutputStream(new ExtendedBufferedOutputStream(decompressedFileOutputStream));
		} catch(FileNotFoundException e) {
			AppView.outputLine("!����: ���� ���� ������ �� �� �����ϴ�.");
			throw e;
		}
	}

	private void openCompressedInputStream() throws IOException{
		try {
			FileInputStream compressedFileInputStream = new FileInputStream(compressedFile());
			setCompressedInputStream(new ExtendedBufferedInputStream(compressedFileInputStream));
		} catch(FileNotFoundException e) {
			AppView.outputLine("!����: ���� ������ �� �� �����ϴ�.");
			throw e;
		}
	}


	private void writeDecompressedBits() throws IOException {
		try { 
			long numberOfBitsOfCompressedData = readNumberOfBitsOfCompressedData();
			for(int i = 0; (long)i < numberOfBitsOfCompressedData; i++) {
				int decodedBit = huffmanDecoder().decodeBit(bitInputManager().readBit());
				if(decodedBit != -1) {
					decompressedOutputStream().write(decodedBit);
				}
			}
		} catch(IOException e) {
			AppView.outputLine("!����: ���� ���� ���Ͽ� ��Ʈ ������ ���⸦ �����߽��ϴ�.");
			throw e;
		}
	}
	
	private void closeCompressedInputStream() throws IOException {
		try {
			compressedInputStream().close();
		} catch(IOException e) {
			AppView.outputLine("!����: ���� ���� �ݱ⸦ �����߽��ϴ�.");
			throw e;
		}
	}

	private void closeDecompressedOutputStream() throws IOException {
		try {
			decompressedOutputStream().close();
		} catch(IOException e) {
			AppView.outputLine("!����: ���� ���� ���� �ݱ⸦ �����߽��ϴ�.");
			throw e;
		}
	}


	private void decompress() throws IOException {
		openCompressedInputStream();
		openDecompressedOutputStream();
		
		setBitInputManager(new BitInputManager(compressedInputStream()));
		
		short serializedHuffmanTree[][] = readSerializedHuffmanTree();
		
		setHuffmanDecoder(new HuffmanDecoder(serializedHuffmanTree));
		
		writeDecompressedBits();
		
		closeCompressedInputStream();
		closeDecompressedOutputStream();
	}

	private void showStatistics()
	{
		AppView.outputLine("> ���� ���� ����:");
		long compressedFileSize = this.compressedFile().length();
		long decompressedFileSize = this.decompressedFile().length();
		AppView.outputLine("- ���� ����: " + this.compressedFile().getAbsolutePath() + 
				" (" + compressedFileSize + " Byte)");
		AppView.outputLine("- ���� ���� ����: " + this.decompressedFile().getAbsolutePath() + 
				" (" + decompressedFileSize + " Byte)");
	}
	
	protected DecompressionController() {
		//
	}

	public void run() {
		if(initCompressedFile()) {
			
			initDecompressedFile();
			
			try {
				decompress();
				AppView.outputLine("");
				AppView.outputLine("! ���� ������ ���������� ���ƽ��ϴ�.");
				showStatistics();
			} catch(IOException e) {
				AppView.outputLine("!����: ���� ������ �����ϴ� ���ȿ� ���� ó�� ������ �߻��߽��ϴ�.");
			}
			
		}
	}
}
