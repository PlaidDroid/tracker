package tracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

class DBHelper {
	private static DB db;

	DBHelper() throws Exception {
		db = new DB();
	}

	void printAll() {
		if (!db.isEmpty()) {
			Iterator it = db.entrySet().iterator();
			while (it.hasNext()) {
//				HashMap.Entry obj = (Entry) it.next();
//				System.out.println(obj.getValue());
				System.out.println(it.next());
			}
		}
	}

	HashMap<String, Integer> getDataFor(LocalDate date) {
		if (!db.isEmpty()) {
			if (db.containsKey(date)) {
				return db.get(date);
			}
		}
		return null;
	}

}
