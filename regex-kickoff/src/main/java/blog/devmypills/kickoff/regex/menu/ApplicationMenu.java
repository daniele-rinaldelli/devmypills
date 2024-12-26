package blog.devmypills.kickoff.regex.menu;

import java.util.Arrays;

public class ApplicationMenu {

	private static final String MENU_CONTENT =
			"""	
			|--- MAIN MENU -----------------------------------
			  1 > Find property
			  2 > Help
			  3 > Exit
			|-------------------------------------------------
			""";

	public static final String HELP_CONTENT = """
				The appliacation help you to find a property path inside a json file.
				
				It is necessary to specify the property you want to find comprhensive of double quotes (es. "propName")
				
				In case you wanto to specify the json content directly you can use -Dcontext input param otherwise you will be asked 
				the json file path on local file system.
				 
				ex. java -jar App.jar -Dcontext={\"prop\": \"value\"} 
				
				The json file must addhere to the json specifications, no controls are made on json correctness at the time.  
				""";

	public enum MenuEntry {
		FIND_PROPERTY(1),
		HELP(2),
		EXIT(3),
		NOT_IMPLEMENTED(-1);

		private final int entryId;

		MenuEntry(int entry) {
			this.entryId = entry;
		}

		@Override
		public String toString() {
			return this.entryId + ". " + this.name().toLowerCase();
		}
	}

	public static String menuContent() {
		return MENU_CONTENT;
	}

	public static MenuEntry getMenuEntryById(String entryId) {
		try {
			return getMenuEntryById(Integer.parseInt(entryId));
		} catch (NumberFormatException ex) {
			return MenuEntry.NOT_IMPLEMENTED;
		}
	}

	public static MenuEntry getMenuEntryById(int entryId) {
		return Arrays.stream(MenuEntry.values())
				.filter(menuEntry -> menuEntry.entryId == entryId)
				.findFirst()
				.orElse(MenuEntry.NOT_IMPLEMENTED);
	}

}
