
package no.hials.jiop.util;

import java.io.Serializable;

/**
 * Utility for normalization
 * http://www.heatonresearch.com/wiki/Range_Normalization 
 * @author Jeff Heaton
 */
public final class NormalizationUtility implements Serializable {

    private final double _dataHigh;
    private final double _dataLow;
    private final double _normalizedHigh;
    private final double _normalizedLow;

    /**
     * Construct the normalization utility. Default to normalization range of 0
     * to +1.
     *
     * @param dataHigh The high value for the input data.
     * @param dataLow The low value for the input data.
     */
    public NormalizationUtility(double dataHigh, double dataLow) {
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
    public NormalizationUtility(double dataHigh, double dataLow, double normalizedHigh, double normalizedLow) {
        this._dataHigh = dataHigh;
        this._dataLow = dataLow;
        this._normalizedHigh = normalizedHigh;
        this._normalizedLow = normalizedLow;
    }

    /**
     * Normalize x.
     *
     * @param x The value to be normalized.
     * @return The result of the normalization.
     */
    public double normalize(double x) {
        return ((x - _dataLow)
                / (_dataHigh - _dataLow))
                * (_normalizedHigh - _normalizedLow) + _normalizedLow;
    }

    /**
     * Denormalize x.
     *
     * @param x The value to denormalize.
     * @return The de-normalized value.
     */
    public double denormalize(double x) {
        return ((_dataLow - _dataHigh) * x - _normalizedHigh
                * _dataLow + _dataHigh * _normalizedLow)
                / (_normalizedLow - _normalizedHigh);
    }
}
