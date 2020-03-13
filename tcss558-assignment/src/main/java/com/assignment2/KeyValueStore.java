package com.assignment2;
import java.util.Hashtable;
/**
 * KeyValueStore class implements a hash table to be used for storage by
 * a server node. This class can be utilized by other class nodes to
 * implement storage for different protocols and process queries.
 */
public class KeyValueStore {
    //The hash table used for key-value storage
    private Hashtable<String, String> storage;
    //Initializes the hash table when an instance of KeyValueStore is initialized
    public KeyValueStore(){
        storage = new Hashtable<String, String>();
    }
    /**
     * Inserts a key value pair into storage.
     *
     * @param key the string key at which the client would like to place the value
     * @param value the integer value to be placed
     */
    public synchronized void putKeyValue(String key, String value) {
        storage.put(key, value);
    }
    /**
     *Returns a value located at a key given by the client.
     *
     * @param key the string key at which the client would like to retrieve a value
     * @return the integer value mapped by that key
     */
    public String getValue(String key) {
        String value = storage.get(key);
        return value;
    }
    /**
     * Removes a key-value pair from storage based on key specified by the client.
     *
     * @param key the key for the key-value pair to be removed from storage
     */
    public synchronized void deleteKey(String key) {
        storage.remove(key);
    }
    /**
     * Prints the contents of the key-value storage.
     */
    public String store() {
        String str = null;
        StringBuilder sb = new StringBuilder();
        if(!storage.isEmpty()) {
            for (String key : storage.keySet()) {
                sb.append("\nkey:" + key + ":value:" + storage.get(key));
            }
            str = sb.toString();
        }
        else
            str = "Key-value store is empty";
        return str;
    }
}
