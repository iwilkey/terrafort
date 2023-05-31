package dev.iwilkey.terrafort.utilities;

/**
 * Represents a generic pair of values.
 *
 * @param <A> the type of the first value
 * @param <B> the type of the second value
 */
public final class Pair<A, B> {

    private A first;
    private B second;

    /**
     * Constructs a new Pair with the specified values.
     *
     * @param first  the first value of the pair
     * @param second the second value of the pair
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value of the pair.
     *
     * @return the first value
     */
    public A getFirst() {
        return first;
    }

    /**
     * Returns the second value of the pair.
     *
     * @return the second value
     */
    public B getSecond() {
        return second;
    }

    /**
     * Sets the first value of the pair.
     *
     * @param first the first value to set
     */
    public void setFirst(A first) {
        this.first = first;
    }

    /**
     * Sets the second value of the pair.
     *
     * @param second the second value to set
     */
    public void setSecond(B second) {
        this.second = second;
    }
}
