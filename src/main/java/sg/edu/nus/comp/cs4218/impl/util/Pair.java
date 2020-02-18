package sg.edu.nus.comp.cs4218.impl.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * A convenience utility class to represent name-value pairs.
 * @param <K> Data Type of the supplied key
 * @param <V> Data Type of the supplied value
 */
public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    /**
     * Construct a new pair
     * @param key The key for this pair
     * @param value The value for this pair
     */
    public Pair(K key, V value) {
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
        hash = 31 * hash + (key != null ? key.hashCode() : 0);
        hash = 31 * hash + (value != null ? value.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (!Objects.equals(key, pair.key)) {
                return false;
            }
            return Objects.equals(value, pair.value);
        }
        return false;
    }
}
