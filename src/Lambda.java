import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;


/**
 * The class {@code Lambda} contains methods to deal with random sampling from various
 * distributions, counting, pseudo-random number generation, as well as other functionalities.
 *
 * <p>Many methods from this class are/were inspired by {@link Math#random()} and by
 * {@code numpy.random} (the {@code python} module).
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
     * @return seed
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

    /**
     * Returns an array of a given size where each element is uniformly distributed as seen in
     * {@link Lambda#uniform(double, double)}. The approach is done using streams, where a
     * stream is created, then for every value in the stream maps it to a uniform sample.
     *
     * @param   size - size of the array returned
     * @param   low - the lowest value in the uniform distribution
     * @param   high - the highest value in the uniform distribution
     * @return  array of uniform samples ~ U[low, high]
     */
    public static double[] uniform(double low, double high, int size) {
        /* Handles faulty input */
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        else if (size == 0) return new double[0];

        /* Makes use of streams to generate uniform samples */
        return Arrays.stream(new double[size])
                .map(i -> uniform(low, high))
                .toArray();
    }

    /**
     * Overloaded method of {@link Lambda#uniform(double, double, int)} however returns an array
     * of realisations of the <i>standard uniform distribution</i>.
     *
     * @param   size - sized of the returned array
     * @return  array of standard uniform realisations.
     */
    public static double[] uniform(int size) {
        return uniform(0, 1, size);
    }

    /**
     * Samples from the <i>exponential distribution</i> where the lambda represents the parameter
     * for the exponential distribution. This is done by using the inverse sampling technique.
     *
     * @param   lambda - parameter for exponential distribution.
     * @return  sample from exponential distribution.
     */
    public static double exponential(double lambda) {
        if (lambda < 0) throw new IllegalArgumentException("Lambda must be greater than 0");
        return -(1 / lambda) * Math.log(uniform());
    }

    /**
     * Generates an array of realisations of the exponential distribution of a given size.
     *
     * @param   lambda - parameter for exponential distribution
     * @param   size - size of
     * @return  array of samples from the exponential distribution.
     */
    public static double[] exponential(double lambda, int size) {
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        return Arrays.stream(new double[size])
                .map(i -> exponential(lambda))
                .toArray();
    }

    /**
     * Generates a realisation of a bernoulli random variable with a given parameter p.
     *
     * @param   p - probability of success
     * @return  returns 1 if success, else 0.
     */
    public static int bernoulli(double p) {
        if (!(0 <= p || p <= 1)) throw new IllegalArgumentException("0 <= p <= 1 must hold.");
        return (uniform() < p) ? 1 : 0;
    }

    /**
     * Generates a realisation of a binomial random variable with parameters <i>n</i> and <i>p</i>.
     *
     * @param   n - amount of samples
     * @param   p - probability of success for each sample
     * @return  sum of each bernoulli trial
     */
    public static int binomial(int n, double p) {
        if (n == 0) return 0;
        return bernoulli(p) + binomial(n-1, p);
    }

    /**
     * Generates an array of realisations from {@link Lambda#binomial(int, double)}.
     * 
     * @param   n - amount of samples
     * @param   p - probability of success
     * @param   size - size of the array given
     * @return  array of sums of each bernoulli trial
     */
    public static int[] binomial(int n, double p, int size) {
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        return Arrays.stream(new int[size])
                .map(i -> binomial(n, p))
                .toArray();
    }


    /**
     * Returns a sample from the normal distribution with given parameters <i>mu</i> and
     * <i>sigma</i>. As the normal has a difficult cumulative distributive function to work with,
     * inverse sampling was not used. As an alternative a <i>Box-Muller transformation</i> was
     * performed, where there is a slight lack of accuracy which is traded for performance.
     *
     * @param   mu - mean of the normal distribution
     * @param   sigma - standard deviation of the normal distribution
     * @return  sample of the given normal distribution ~ N(mu, sigma)
     */
    public static double normal(double mu, double sigma) {
        double u = 2 * uniform() - 1;
        double v = 2 * uniform() - 1;
        double r = u * u + v * v;

        /* If outside interval [0, 1] recalls the function */
        if (r == 0 || r >=1) return normal(mu, sigma);
        return mu + ((u * (Math.sqrt(-2 * Math.log(r)/r))) * sigma);
    }

    /**
     * Returns a sample from the standard normal distribution using
     * {@link Lambda#normal(double, double)}. The standard normal distribution in this case is
     * defined where {@code mu = 0} and {@code sigma = 1}.
     *
     * @return sample from standard normal distribution
     */
    public static double normal() {
        return normal(0, 1);
    }

    /**
     * Returns an array of normal distribution samples using the <i>Box-Muller transformation</i>
     * as mentioned in {@link Lambda#normal(double, double)}.
     *
     * @param   mu - mean of the normal distribution
     * @param   sigma - standard deviation of the normal distribution
     * @param   size - size of the array returned
     * @return  sample of the given normal distribution ~ N(mu, sigma)
     */
    public static double[] normal(double mu, double sigma, int size) {
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        return Arrays.stream(new double[size])
                .map(i -> normal(mu, sigma))
                .toArray();
    }

    /**
     * Returns an array similar to {@link Lambda#normal(double, double, int)} however samples
     * standard normal realisations.
     *
     * @param   size - size of the array returned
     * @return  array standard normal samples
     */
    public static double[] normal(int size) {
        return normal(0, 1, size);
    }

    /**
     * Returns a numerical approximation of the error function (erf x). The numerical
     * approximation comes from the mathematical reference work Abramowitz and Stegun (AS)
     * (informal name). The full title of the work Handbook of Mathematical Functions with Formulas,
     * Graphs, and Mathematical Table.
     *
     * The numerical method used can be found in chapter 7.1 (specifically equation 26). The
     * approximation has a maximum error of {@code 5 x 10^(-4)}, however this is not taking into
     * other errors related to floating point errors etc.
     *
     * @param   x - input for the error function
     * @return  output of the error function
     */
    public static double erf(double x) {
        double t = 1/(1 + 0.47047 * x);
        double alpha1 = 0.3480242;
        double alpha2 = -0.0958798;
        double alpha3 =  0.7478556;

        // Application of Abramowitz and Stegun equation 7.1.26
        return 1 - (alpha1 * t + alpha2 * Math.pow(t, 2) + alpha3 * Math.pow(t, 3))
                * Math.exp(-Math.pow(x, 2));
    }

    /**
     * This method imitates the {@code pnorm} function in {@code R}. To be more specific, the
     * method finds the cumulative distribution function (CDF) at the point {@code x} for a given
     * mu and sigma. In other words, gets the probability that a random sample is smaller than the
     * given {@code x}.
     *
     * This method uses the error function approximation found at {@link Lambda#erf(double)} in
     * order to relate the error function and the CDF of a normal distribution.
     *
     * @param   x - input for the function
     * @param   mu - mean of the normal distribution
     * @param   sigma - standard deviation of the normal distribution
     * @return  approximation to the CDF of a normal distribution at {@code x}
     */
    public static double pnorm(double x, double mu, double sigma) {
        // Relating the error function to the CDF of the normal distribution
        return 0.5 * (1 + erf((x - mu) / (Math.sqrt(2) * sigma)));
    }

    /**
     * Overloaded method of {@link Lambda#pnorm(double)} for the standard normal distribution
     * (where {@code mu = 0} and {@code sigma = 1}).
     *
     * @param   x - input for the function
     * @return  approximation to the CDF of a normal distribution at {@code x}
     */
    public static double pnorm(double x) {
        // Relating the error function to the CDF of the normal distribution
        return 0.5 * (1 + erf((x) / (Math.sqrt(2))));
    }

    /**
     * Samples a geometric distribution where <i>k</i> starts at {@code 0}. This is performed
     * using the inverse transform sampling method.
     *
     * @param   p - success probability
     * @return  sample from geometric distribution with parameter p
     */
    public static int geometric(double p) {
        if (!(0 <= p || p <= 1)) throw new IllegalArgumentException("0 <= p <= 1 must hold.");
        return (int) Math.floor(Math.log(1 - uniform()) / Math.log(1 - p));
    }

    /**
     * Samples a geometric distribution and puts the results into an array which is then returned
     * . The functionality of the geometric distribution can be found
     * {@link Lambda#geometric(double)} here.
     *
     * @param   p - success probability
     * @param   size - size of the array returned
     * @return  sample from geometric distribution with parameter p
     */
    public static int[] geometric(double p, int size) {
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        return Arrays.stream(new int[size])
                .map(i -> geometric(p))
                .toArray();
    }

    /**
     * Samples a random poisson random variable. As the cumulative distributive function of a
     * poisson random variable is not invertible, thus a simulation is done using the definition
     * of a poisson random variable.
     *
     * @param   lambda - parameter for the distribution
     * @return  sample poisson random variable
     */
    public static int poisson(double lambda) {
        int sum = 0;
        int n = -1;

        while (sum < 1) {
            sum += exponential(lambda);
            n++;
        }
        return n;
    }

    /**
     * Returns an array of poisson random variables, where each sample comes can be seen
     * {@link Lambda#poisson(double)}.
     *
     * @param   lambda - parameter for the distribution
     * @param   size - size of the array returned
     * @return  array consisting of independent samples from the poisson distribution
     */
    public static int[] poisson(double lambda, int size) {
        if (size < 0) throw new IllegalArgumentException("Size must be a non-negative integer.");
        return Arrays.stream(new int[size])
                .map(i -> poisson(lambda))
                .toArray();
    }

    /**
     * Method that deals with getting all possible permutations of an array of integers. The
     * output is put into a list of lists to store all the different permutations. The solution
     * is done recursively, making use of the helper method.
     * {@link Lambda#collectPermutations(int[], int, List, List)}. Inspiration for this solution
     * came from
     * <a href="https://leetcode.com/problems/permutations/discuss/18436/Java-Clean-Code-Two-recursive-solutions">a leetcode problem</a>.
     *
     * @param   numbers - array of numbers
     * @return  list of lists of all permutations of the array given
     */
    public static List<List<Integer>> permute(int[] numbers) {
        List<List<Integer>> permutations = new ArrayList<>();
        if (numbers.length == 0) {
            return permutations;
        }

        collectPermutations(numbers, 0, new ArrayList<>(), permutations);
        return permutations;
    }

    /**
     * Helper method for {@link Lambda#permute(int[])} that deals with the recursive element of
     * the permutations.
     *
     * @param   numbers - array of number
     * @param   start - stores index
     * @param   permutation - list of integers that is used to build the permutation
     * @param   permutations - list of lists containing the output
     */
    private static void collectPermutations(int[] numbers, int start, List<Integer> permutation,
            List<List<Integer>>  permutations) {

        if (permutation.size() == numbers.length) {
            permutations.add(permutation);
            return;
        }

        for (int i = 0; i <= permutation.size(); i++) {
            List<Integer> newPermutation = new ArrayList<>(permutation);
            newPermutation.add(i, numbers[start]);
            collectPermutations(numbers, start + 1, newPermutation, permutations);
        }
    }
}
