/*Asmita Singla
 *Sonia Xu
 *558 Applied Distributed Systems - Assignment 1 
 */

import java.util.HashMap;

public class StoreImplementation {

	private HashMap<String, String> storeMap = new HashMap<>();
	String value;
	String key;

	StoreImplementation() {

		this.storeMap = storeMap;

	}

	// Put key-values in a hash-map
	public synchronized HashMap<String, String> putValuesInStrore(String[] taskKeyValue) {
		key = taskKeyValue[1];
		value = taskKeyValue[2];

		storeMap.put(key, value);

		return storeMap;
	}

	// Get values for the given key
	public String getValuesFromStore(String[] taskKeyValue) {

		if (storeMap.get(taskKeyValue[1]) != null) {
			String value = storeMap.get(taskKeyValue[1]);
			return value;
		}
		return null;
	}

	// Delete key-value from hash-map
	public synchronized HashMap<String, String> deleteValuesFromStore(String[] taskKeyValue) {

		if (storeMap.get(taskKeyValue[1]) != null) {
			storeMap.remove(taskKeyValue[1]);
		}

		return storeMap;
	}

}
