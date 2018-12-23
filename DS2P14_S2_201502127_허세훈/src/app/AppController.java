package app;

public class AppController {

	private CompressionController _compressionController;
	private DecompressionController _decompressionController;
	private ValidationController _validationController;
	
	private CompressionController compressionController() {
		if(this._compressionController == null) {
			this._compressionController = new CompressionController();
		}
		return this._compressionController;
	}
	
	private DecompressionController decompressionController() {
		if(this._decompressionController == null) {
			this._decompressionController = new DecompressionController();
		}
		return this._decompressionController;
	}
	
	private ValidationController validationController() {
		if(this._validationController == null) {
			this._validationController = new ValidationController();
		}
		return this._validationController;
	}
	
	private MainMenu selectMenu() {
		AppView.outputLine("");
		AppView.output("? �ؾ��� �۾��� �����Ͻÿ� (����=1, ����=2, ����=3, ����=4): ");
		try {
			int selectedMenuNumber = AppView.inputInteger();
			MainMenu selectedMenuValue = MainMenu.value(selectedMenuNumber);
			if(selectedMenuValue == MainMenu.ERROR) {
				AppView.outputLine("!����: �۾� ������ �߸��Ǿ����ϴ�. (�߸��� ��ȣ: " + selectedMenuNumber + ")");
			}
			return selectedMenuValue;
		}
		catch(NumberFormatException e) {
			AppView.outputLine("!����: �Էµ� �޴� ��ȣ�� ������ ���ڰ� �ƴմϴ�.");
			return MainMenu.ERROR;
		}
	}
	
	public void run() {
		AppView.outputLine("<<< Huffman Code �� �̿��� ���� ����/���� ���α׷��� �����մϴ�. >>>");
		
		MainMenu selectedMenuValue = this.selectMenu();
		while(selectedMenuValue != MainMenu.END) {
			switch(selectedMenuValue) {
			case COMPRESS:
				this.compressionController().run();
				break;
			case DECOMPRESS:
				this.decompressionController().run();
				break;
			case VALIDATE:
				this.validationController().run();
				break;
			default:
				break;	
			}
			selectedMenuValue = this.selectMenu();
		}
		AppView.outputLine("");
		AppView.outputLine("<<< uffman Code �� �̿��� ���� ����/���� ���α׷��� �����մϴ� >>>");
	}
}
