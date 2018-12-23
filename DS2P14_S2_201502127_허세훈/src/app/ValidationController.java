package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class ValidationController {

	private File _firstFile;
	private File _secondFile;
	
	private String _firstFilePath;
	
	private BufferedInputStream _firstInputStream;
	private BufferedInputStream _secondInputStream;
	
	private File firstFile() {
		return this._firstFile;
	}

	private void setFirstFile(File newFirstFile) {
		this._firstFile = newFirstFile;
	}

	private File secondFile() {
		return this._secondFile;
	}

	private void setSecondFile(File newSecondFile) {
		this._secondFile = newSecondFile;
	}

	private String firstFilePath() {
		return this._firstFilePath;
	}
	
	private void setFirstFilePath(String newFirstFilePath) {
		this._firstFilePath = newFirstFilePath;
	}

	private BufferedInputStream firstInputStream() {
		return this._firstInputStream;
	}

	private void setFirstInputStream(BufferedInputStream newFirstInputStream) {
		this._firstInputStream = newFirstInputStream;
	}

	private BufferedInputStream secondInputStream() {
		return this._secondInputStream;
	}

	private void setSecondInputStream(BufferedInputStream newSecondInputStream) {
		this._secondInputStream = newSecondInputStream;
	}

	private boolean initFirstFile() {
		AppView.outputLine("");
		AppView.outputLine("? 첫 번째 파일의 경로와 이름을 입력하시오: ");
        
		setFirstFilePath(AppView.inputFilePath());
		String firstFileName = AppView.inputFileName();
		String filePathAndName = 
				(new StringBuilder(String.valueOf(firstFilePath()))).append("/").append(firstFileName).toString();
		setFirstFile(new File(filePathAndName));
		
		if(firstFile().exists()) {
			return true;
		} else {
			AppView.outputLine((new StringBuilder("!오류: 파일 (")).append(filePathAndName).append(") 이 존재하지 않습니다.").toString());
			return false;
		}
	}

	private boolean initSecondFile() {
		AppView.outputLine("");
		AppView.outputLine("? 두 번째 파일의 경로와 이름을 입력하시오: ");
		String filePath;
		
		if(AppView.inputAnswerForUsingSamePath()) {
			filePath = firstFilePath();	
		} else {
			filePath = AppView.inputFilePath();
		}
		
		String fileName = AppView.inputFileName();
		String filePathAndName = 
				(new StringBuilder(String.valueOf(filePath))).append("/").append(fileName).toString();
		setSecondFile(new File(filePathAndName));
		
		if(secondFile().exists()) {
			return true;
		} else {
			AppView.outputLine((new StringBuilder("!오류: 파일 (")).append(filePathAndName).append(") 이 존재하지 않습니다.").toString());
			return false;
		}
	}

	private int readByteFromFirstInputStream() throws IOException {
		try
		{
			return firstInputStream().read();
		}
		catch(IOException e)
		{
			AppView.outputLine("!오류: 첫번째 파일 읽기를 실패했습니다.");
			throw e;
		}
	}

	private int readByteFromSecondInputStream() throws IOException {
		try {
			return secondInputStream().read();
		} catch(IOException e) {
			AppView.outputLine("!오류: 두번째 파일 읽기를 실패했습니다.");
			throw e;
		}
	}

	private boolean validate() throws IOException {
		setFirstInputStream(new BufferedInputStream(new FileInputStream(firstFile())));
		setSecondInputStream(new BufferedInputStream(new FileInputStream(secondFile())));
        
		int byteCodeFirst = readByteFromFirstInputStream();
		int byteCodeSecond;
		
		byteCodeSecond = readByteFromSecondInputStream();
		
		//
		while(byteCodeFirst == byteCodeSecond) {
			byteCodeFirst = readByteFromFirstInputStream();
			byteCodeSecond = readByteFromSecondInputStream();
			
			if(byteCodeFirst == -1 || byteCodeFirst == -1)
				break;
		}

		return (byteCodeFirst == -1 && byteCodeSecond == -1);
	}

	protected ValidationController()
	{
		
	}

	protected void run()
	{
		if(initFirstFile() && initSecondFile()) {
			try	{
				if(validate())
					AppView.outputLine("> 두 파일의 내용은 동일합니다.");
				else
					AppView.outputLine("> 두 파일의 내용은 동일하지 않습니다.");
			} catch(IOException e) {
				AppView.outputLine("!오류: 검증을 실행하는 동안에 파일 처리 오류가 발생했습니다.");
			}
		}
	}
}
