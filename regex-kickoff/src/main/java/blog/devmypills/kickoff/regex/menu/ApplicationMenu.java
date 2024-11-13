package blog.devmypills.kickoff.regex.menu;

import java.util.Arrays;

public class ApplicationMenu {

	private static final String MENU_CONTENT =
			"""	
			|--- MAIN MENU -----------------------------------
			  1 > Find property
			  2 > Exit
			|-------------------------------------------------
			""";

	public enum MenuEntry {
		FIND_PROPERTY(1),
		EXIT(2),
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
