package src;

import java.util.Objects;

public class MyPair<K, V> {
    private final K key;
    private final V value;

    public MyPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyPair<?, ?> myPair = (MyPair<?, ?>) o;
        return Objects.equals(key, myPair.key) && Objects.equals(value, myPair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
