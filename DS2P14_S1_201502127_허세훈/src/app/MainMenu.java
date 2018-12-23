package app;

public enum MainMenu {

	ERROR,
	COMPRESS,
	DECOMPRESS,
	VALIDATE,
	END;
	
	public static MainMenu value(int menuNumber) {
		if(menuNumber <= 0 || menuNumber > END.ordinal()) {
			return MainMenu.ERROR;
		}
		else {
			return MainMenu.values()[menuNumber];
		}
	}
}
