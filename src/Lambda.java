import java.util.Arrays;

/**
 * <i>joe</i>
 */
public final class Lambda {

    /**
     * Private constructor for the class.
     */
    private Lambda() {}

    /**
     * Famous mathematical constant which has numerous applications/definitions, however in this
     * context used to define the <i>exponential distribution</i> and the <i>normal
     * distribution</i>.
     */
    public static final double E = 2.7182818284590452354;

    /**
     * Mathematical constant that is typically defined as the ratio of the circumference of a
     * circle to its diameter.
     */
    public static final double PI = 3.14159265358979323846;

    private long seed = 0;

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

    /**
     * Returns an {@code array} of a given size of pseudo-random number generated numbers,
     * where the numbers in the array are based on the seed given. The returned array are of long
     * type (64 bit). For the integer array see {@link Lambda#lcg32Array(int, int, int, int)}.
     *
     * @param   seed - seed of the linear congruential generator (X_{n-1})
     * @param   multiplier - multiplier (a)
     * @param   increment - increment (c)
     * @param   size - size of the resulting array
     * @return  array of pseudo-random numbers
     */
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

    /**
     * Similar to {@link Lambda#lcg64Array(long, long, long, int)} however uses a fixed
     * multiplier and increment which optimises the period.
     *
     * @param   seed - seed of the linear congruential generator (X_{n-1})
     * @param   size - size of the resulting array
     * @return  array of pseudo-random numbers
     */
    public static long[] lcg64Array(long seed, int size) {
        return lcg64Array(seed, 5428252657583070383L, 0, size);
    }

    /**
     * Returns array of pseudo random numbers alike
     * {@link Lambda#lcg64Array(long, long, long, int)}, however returns an array of integers
     * rather than longs (hence 32-bit).
     *
     * @param   seed - seed of the linear congruential generator (X_{n-1})
     * @param   multiplier - multiplier (a)
     * @param   increment - increment (c)
     * @param   size - size of the resulting array
     * @return  array of pseudo-random numbers
     */
    public static int[] lcg32Array(int seed, int multiplier, int increment, int size) {
        return Arrays.stream(lcg64Array(seed, multiplier, increment, size))
                .mapToInt(i -> (int) i)
                .toArray();
    }

    /**
     * Returns a seed value in the form of a {@code long} using {@link System#nanoTime()}. As a
     * restriction of the seed is to be positive, the output of {@link System#nanoTime()} will
     * always be positive.
     *
     * @return  seed
     */
    public static long generateSeed() {
        long time = System.nanoTime();
        return (time < 0) ? -time : time;
    }

    /**
     * Produces a realisation from the standard uniform distribution (where the samples are
     * uniformly distributed from [0, 1]).
     *
     * @return  sample from standard uniform distribution
     */
    public static double uniform() {
        double output = ((double) lcg64(generateSeed()))/ Long.MAX_VALUE;
        return (output < 0) ? -output : output;
    }

    /**
     * Produces a sample of a uniform distribution on U[low, high] by transforming the standard
     * uniform distribution to fit the range while still maintain uniformity.
     *
     * @param   low - the lowest value in the uniform distribution
     * @param   high - the highest value in the uniform distribution
     * @return  sample of uniform distribution ~ U[low, high]
     */
    public static double uniform(double low, double high) {
        return uniform() * (high - low) + low;
    }
}
