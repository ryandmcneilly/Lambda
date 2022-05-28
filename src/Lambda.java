import java.util.Arrays;

/**
 * <i>joe</i>
 */
public final class Lambda {

    /**
     * Private constructor for the class
     */
    private Lambda() {}

    public static final double E = 2.7182818284590452354;

    public static final double PI = 3.14159265358979323846;

    /**
     * Returns pseudo-random double using a <i>linear congruential generator</i> which outputs
     * value from {@code -2147483647} to {@code 2147483647}, however this depends on the
     * parameters given. To see more on <i>linear congruential generators</i> refer to
     * <a href="https://en.wikipedia.org/wiki/Linear_congruential_generator">the wiki</a>.
     *
     *
     * @param    seed - seed of the lcg, otherwise known as 'X_0'
     * @param    multiplier - multiplier of the lcg, otherwise known as 'a'.
     * @param    increment - increment of the lcg, otherwise known as 'm'
     * @param    modulus - modulus of the lcg, otherwise known as 'm'
     * @return   pseudo-random number.
     */
    public static long lcg(long seed, long multiplier, long increment, long modulus) {
        return (multiplier * seed + increment) % modulus;
    }

    /**
     * Similar to {@link Lambda#lcg(long, long, long, long)} however uses a custom multiplier and
     * modulus with no increment. The choice of the multiplier and modulus comes from a study
     * that can be seen
     * <a href="https://www.researchgate.net/publication/220258210_An_exhaustive_search_for_good_64-bit_linear_congruential_random_number_generators_with_restricted_multiplier">here.</a>
     * @param   seed - seed of the lcg, otherwise known as 'X_0'.
     * @return  64-bit pseudo-random-number in form of a long.
     */
    public static long lcg64(long seed) {
        return Lambda.lcg(
                seed,
                5428252657583070383L,
                0,
                Long.MAX_VALUE - 4568);
    }

    /**
     * Implementation similar to {@link Lambda#lcg64(long)} however outputs an integer inherently
     * making the output 32-bit. The choice of parameters is inspired from {@code minstd_rand}, a
     * pseudo-random number generator commonly-used throughout {@code C++11}.
     *
     * @param   seed - seed of the lcg, otherwise known as 'X_0'.
     * @return  32-bit pseudo-random-number in form of an integer.
     */
    public static int lcg32(int seed) {
        return (int) Lambda.lcg(
                seed,
                48271,
                1,
                Integer.MAX_VALUE);
    }

    public static long[] lcg64Array(long seed, long multiplier, long increment, int size) {
        /* Handles faulty input. */
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        else if (size == 0) return new long[0];

        long[] output = new long[size];
        output[0] = lcg(seed, multiplier, increment, size);

        /* Builds the array */
        int counter = 1;
        while (counter < size) {
            output[counter] = lcg64(counter-1);
            counter++;
        }
        return output;
    }

    public static long[] lcg64Array(long seed, int size) {
        return lcg64Array(seed, 5428252657583070383L, 0, size);
    }

    public static int[] lcg32Array(int seed, int multiplier, int increment, int size) {
        return Arrays.stream(lcg64Array(seed, multiplier, increment, size))
                .mapToInt(i -> i)
                .toArray();
    }
}
