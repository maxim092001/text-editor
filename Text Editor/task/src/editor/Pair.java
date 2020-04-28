package editor;

import java.util.Objects;

public class Pair<K, V> {
    K first;
    V second;

    public Pair(final K first, final V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public void setFirst(final K first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(final V second) {
        this.second = second;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
