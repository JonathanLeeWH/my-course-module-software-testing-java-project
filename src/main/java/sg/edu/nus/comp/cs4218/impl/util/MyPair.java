package sg.edu.nus.comp.cs4218.impl.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * A convenience utility class to represent name-value pairs.
 * @param <K> Data Type of the supplied key
 * @param <V> Data Type of the supplied value
 */
public class MyPair<K, V> implements Serializable {
    private final K key;
    private final V value;

    /**
     * Construct a new pair
     * @param key The key for this pair
     * @param value The value for this pair
     */
    public MyPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "(" + key + "," + value + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (key == null ? 0 : key.hashCode());
        hash = 31 * hash + (value == null ? 0 : value.hashCode());
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MyPair) {
            MyPair myPair = (MyPair) obj;
            if (!Objects.equals(key, myPair.key)) {
                return false;
            }
            return Objects.equals(value, myPair.value);
        }
        return false;
    }
}
