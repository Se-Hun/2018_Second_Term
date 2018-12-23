package app;
import java.util.Scanner;

public final class AppView {
	
	private static Scanner scanner = new Scanner(System.in);
	
	private static final boolean DEBUG_MODE = true;
	
	private AppView() {
		
	}
	
	public static void outputDebugMessage(String aString) {
		if(DEBUG_MODE) {
			System.out.print(aString);
		}
	}
	
	public static void outputLine(String aString) {
		System.out.println(aString);
	}
	
	public static void output(String aString) {
		System.out.print(aString);
	}
	
	public static void DebugPrint(String aString) {
		System.out.println(aString);
	}
	
	public static void outputErrorLine(String aString) {
		System.out.println(aString);
	}
	
	public static int inputInteger() throws NumberFormatException {
		return Integer.parseInt(AppView.scanner.next());
	}
	
	public static String inputString() {
		return AppView.scanner.next();
	}
	
	public static String inputFileName() {
		AppView.output("? 파일 이름을 입력하시오: ");
		return AppView.scanner.next();
	}
	
	public static String inputFilePath() {
		AppView.output("? 파일 경로를 입력하시오 (현재 폴더를 사용하려면 '.' 을 입력하시오) : ");
		return AppView.scanner.next();
	}
	
	public static boolean inputAnswerForUsingSamePath() {
		AppView.outputLine("? 두번째 파일의 경로를 첫번째 파일의 경로와 동일한 것을 사용하겠습니까? ");
		AppView.output(" 동일한 경로를 사용하려면 'Y' 나 'y' 를 치고, 다른 경로를 사용하려면 공백이 아닌 다른 아무 키나 치시오: ");
		char answer = AppView.scanner.next().charAt(0);
		return (answer == 'Y' || answer == 'y');
	}
	
	/*
	public static String inputFileName() {
		String fileName;
		String scannedToken;
		while(true) {
			AppView.output("? 사용할 파일 이름을 입력하시오: ");
			scannedToken = AppView.scanner.next();
			fileName = scannedToken;
			return fileName;
		}
	}
	
	public static String inputFilePath() {
		String filePath;
		String scannedToken;
		while(true) {
			AppView.output("? 파일을 저장할 경로를 입력하시오( . 입력시 프로젝트 경로 사용 ): ");
			scannedToken = AppView.scanner.next();
			filePath = scannedToken;
			return filePath;
		}
	}
	
	public static int inputMaxSize() {
		int maxSize;
		String scannedToken;
		while(true) {
			AppView.output("? 생성할 수의 최대 값을 입력하세요: ");
			scannedToken = AppView.scanner.next();
			try {
				maxSize = Integer.parseInt(scannedToken);
				return maxSize;
			} catch(NumberFormatException e) {
				AppView.outputLine("[오류] 최대 값 입력에 오류가 있습니다: " + scannedToken);
			}
		}
	}*/
}
