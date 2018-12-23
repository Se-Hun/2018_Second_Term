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
		AppView.outputLine("? 압축을 풀 파일의 경로와 이름을 입력하시오: ");
		
		String filePath = AppView.inputFilePath();
		String fileName = AppView.inputFileName();
		String filePathAndName = (new StringBuilder(String.valueOf(filePath))).append("/").append(fileName).toString();
		
		setCompressedFile(new File(filePathAndName));
		
		if(compressedFile().exists()) {
			return true;
        } else {
        	AppView.outputLine((new StringBuilder("!오류: 압축 파일 (")).append(filePathAndName).append(") 이 존재하지 않습니다.").toString());
        	return false;
        }
	}
	
	private void initDecompressedFile() {
		
		AppView.outputLine("");
		String filePathAndName = FilePathManager.getFilePathAndNameWithoutExtension(compressedFile());
		setDecompressedFile(new File(filePathAndName));
		
		if(decompressedFile().exists()) {
			AppView.outputLine((new StringBuilder("!경고: 압축 해제 파일 (")).append(filePathAndName).append(") 이 이미 존재합니다.").toString());
			AppView.outputLine("- 압축 해제 파일의 이름을 다른 것으로 바꾸어 처리합니다:");
			
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
			
			AppView.outputLine((new StringBuilder("- 새로운 압축 해제 파일의 이름은 ")).append(decompressedFilePathAndName).append(" 입니다.").toString());
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
				AppView.outputLine("!오류: [직렬화된 허프만 트리]를 읽는 것을 실패했습니다.");
				throw e;
			}
		}
		
		return serializedHuffmanTree;
	}
	
	private long readNumberOfBitsOfCompressedData() throws IOException {
		try {
			return compressedInputStream().readLong();
		} catch(IOException e) {
			AppView.outputLine("!오류: [압축된 데이터의 비트 수]를 파일에서 읽어 오는 것을 실패했습니다.");
			throw e;
		}
	}
	
	private void openDecompressedOutputStream() throws IOException {
		try {
			FileOutputStream decompressedFileOutputStream = new FileOutputStream(decompressedFile());
			setDecompressedOutputStream(new ExtendedBufferedOutputStream(decompressedFileOutputStream));
		} catch(FileNotFoundException e) {
			AppView.outputLine("!오류: 압축 해제 파일을 열 수 없습니다.");
			throw e;
		}
	}

	private void openCompressedInputStream() throws IOException{
		try {
			FileInputStream compressedFileInputStream = new FileInputStream(compressedFile());
			setCompressedInputStream(new ExtendedBufferedInputStream(compressedFileInputStream));
		} catch(FileNotFoundException e) {
			AppView.outputLine("!오류: 압축 파일을 열 수 없습니다.");
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
			AppView.outputLine("!오류: 압축 해제 파일에 비트 데이터 쓰기를 실패했습니다.");
			throw e;
		}
	}
	
	private void closeCompressedInputStream() throws IOException {
		try {
			compressedInputStream().close();
		} catch(IOException e) {
			AppView.outputLine("!오류: 압축 파일 닫기를 실패했습니다.");
			throw e;
		}
	}

	private void closeDecompressedOutputStream() throws IOException {
		try {
			decompressedOutputStream().close();
		} catch(IOException e) {
			AppView.outputLine("!오류: 압축 해제 파일 닫기를 실패했습니다.");
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
		AppView.outputLine("> 압축 해제 정보:");
		long compressedFileSize = this.compressedFile().length();
		long decompressedFileSize = this.decompressedFile().length();
		AppView.outputLine("- 압축 파일: " + this.compressedFile().getAbsolutePath() + 
				" (" + compressedFileSize + " Byte)");
		AppView.outputLine("- 압축 해제 파일: " + this.decompressedFile().getAbsolutePath() + 
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
				AppView.outputLine("! 압축 해제를 성공적으로 마쳤습니다.");
				showStatistics();
			} catch(IOException e) {
				AppView.outputLine("!오류: 압축 해제를 실행하는 동안에 파일 처리 오류가 발생했습니다.");
			}
			
		}
	}
}
