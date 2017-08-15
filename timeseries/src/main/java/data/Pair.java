package data;

import lombok.NonNull;

/**
 * Represents a 2-tuple.
 *
 * @param <T> the type of the first element.
 * @param <S> the type of the second element.
 */
public final class Pair<T extends Comparable<T>, S extends Comparable<S>>
        implements Comparable<Pair<T, S>> {

    private final T first;
    private final S second;

    static Pair<Integer, Integer> intPair(Integer first, Integer second) {
        return new Pair<>(first, second);
    }

    public static <T extends Comparable<T>, S extends Comparable<S>> Pair<T, S> newPair(T first, S second) {
        return new Pair<>(first, second);
    }

    private Pair(@NonNull T first, @NonNull S second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return this.first;
    }

    public S second() {
        return this.second;
    }

    /**
     * Compare this pair to another pair for lexicographic ordering.
     * The algorithm was adapted from the top answer <a target="_blank" href=
     * "https://stackoverflow.com/questions/5292303/how-does-tuple-comparison-work-in-python">here.</a>
     *
     * @param otherPair the pair to compare this one to.
     * @return an integer value satisfying the {@link Comparable#compareTo(Object)} contract.
     */
    @Override
    public int compareTo(@NonNull Pair<T, S> otherPair) {
        int result = this.first.compareTo(otherPair.first);
        if (result != 0) {
            return result;
        }
        return this.second.compareTo(otherPair.second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

}
