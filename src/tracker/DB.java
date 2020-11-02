package tracker;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Scanner;

class DB extends HashMap<LocalDateTime, HashMap<String, Integer>> {

	// path to the database file
	private static final Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), ".trackerDB");

	// separators used in the file
	private static final String timeSeperator = "@";
	private static final String categorySeperator = "#";
	private static final String valueSeperator = "$";

	DB() throws Exception {
		// create or read database
		if (!Files.exists(path)) {
			Files.createFile(path);
		}
		readToDB();
	}

	private String readFromFile() throws IOException {
		Scanner sc = new Scanner(Files.newInputStream(path));
		String db = "";
		while (sc.hasNext()) {
			db += sc.next();
		}
		return db;
	}

	private void readToDB() throws Exception {
		// read string data
		String db = readFromFile();

		String[] timeDB = db.split(timeSeperator);
		for (String timeData : timeDB) {
			if (timeData.length() != 0) {
				HashMap<String, Integer> category = new HashMap<>();

				// get time
				String strTime = timeData.substring(0, timeData.indexOf(categorySeperator));
				LocalDateTime time = LocalDateTime.parse(strTime);
				timeData = timeData.substring(timeData.indexOf(categorySeperator));

				// split by category
				String[] catDB = timeData.split(categorySeperator);
				for (String catData : catDB) {
					if (catData.length() != 0) {
						// get category name
						String cat = catData.substring(0, catData.indexOf(valueSeperator));
						// get value
						String value = catData.substring(catData.indexOf(valueSeperator) + 1);
						// put in category DB
						category.put(cat, Integer.valueOf(value));
					}
				}
				// put everything in the DB
				this.put(time, category);
			}
		}
	}
}
