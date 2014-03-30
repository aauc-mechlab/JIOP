
package no.hials.jiop;

/**
 * Utility for normalization
 *
 * @author Jeff Heaton
 */
public final class NormUtil {

    private final double dataHigh;
    private final double dataLow;
    private final double normalizedHigh;
    private final double normalizedLow;

    /**
     * Construct the normalization utility. Default to normalization range of 0
     * to +1.
     *
     * @param dataHigh The high value for the input data.
     * @param dataLow The low value for the input data.
     */
    public NormUtil(double dataHigh, double dataLow) {
        this(dataHigh, dataLow, 1, 0);
    }

    /**
     * Construct the normalization utility, allow the normalization range to be
     * specified.
     *
     * @param dataHigh The high value for the input data.
     * @param dataLow The low value for the input data.
     * @param normalizedHigh The high value for the normalized data.
     * @param normalizedLow The low value for the normalized data.
     */
    public NormUtil(double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
        this.dataHigh = dataHigh;
        this.dataLow = dataLow;
        this.normalizedHigh = normalizedHigh;
        this.normalizedLow = normalizedLow;
    }

    /**
     * Normalize x.
     *
     * @param x The value to be normalized.
     * @return The result of the normalization.
     */
    public double normalize(double x) {
        return ((x - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }

    /**
     * Denormalize x.
     *
     * @param x The value to denormalize.
     * @return The de-normalized value.
     */
    public double denormalize(double x) {
        return ((dataLow - dataHigh) * x - normalizedHigh
                * dataLow + dataHigh * normalizedLow)
                / (normalizedLow - normalizedHigh);
    }
}
