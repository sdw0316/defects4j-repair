/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;

import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;

/**
 * Computes the variance of the available values.  By default, the unbiased
 * "sample variance" definitional formula is used: 
 * <p>
 * variance = sum((x_i - mean)^2) / (n - 1) </p>
 * <p>
 * where mean is the {@link Mean} and <code>n</code> is the number
 * of sample observations.</p>
 * <p>
 * The definitional formula does not have good numerical properties, so
 * this implementation does not compute the statistic using the definitional
 * formula. <ul>
 * <li> The <code>getResult</code> method computes the variance using 
 * updating formulas based on West's algorithm, as described in
 * <a href="http://doi.acm.org/10.1145/359146.359152"> Chan, T. F. and
 * J. G. Lewis 1979, <i>Communications of the ACM</i>,
 * vol. 22 no. 9, pp. 526-531.</a></li>
 * <li> The <code>evaluate</code> methods leverage the fact that they have the
 * full array of values in memory to execute a two-pass algorithm. 
 * Specifically, these methods use the "corrected two-pass algorithm" from
 * Chan, Golub, Levesque, <i>Algorithms for Computing the Sample Variance</i>,
 * American Statistician, August 1983.</li></ul>
 * Note that adding values using <code>increment</code> or 
 * <code>incrementAll</code> and then executing <code>getResult</code> will
 * sometimes give a different, less accurate, result than executing 
 * <code>evaluate</code> with the full array of values. The former approach
 * should only be used when the full array of values is not available.</p>
 * <p>
 * The "population variance"  ( sum((x_i - mean)^2) / n ) can also
 * be computed using this statistic.  The <code>isBiasCorrected</code>
 * property determines whether the "population" or "sample" value is
 * returned by the <code>evaluate</code> and <code>getResult</code> methods.
 * To compute population variances, set this property to <code>false.</code>
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If 
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or 
 * <code>clear()</code> method, it must be synchronized externally.</p>
 * 
 * @version $Revision$ $Date$
 */
public class Variance extends AbstractStorelessUnivariateStatistic implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -9111962718267217978L;  
      
    /** SecondMoment is used in incremental calculation of Variance*/
    protected SecondMoment moment = null;

    /**
     * Boolean test to determine if this Variance should also increment
     * the second moment, this evaluates to false when this Variance is
     * constructed with an external SecondMoment as a parameter.
     */
    protected boolean incMoment = true;
    
    /**
     * Determines whether or not bias correction is applied when computing the
     * value of the statisic.  True means that bias is corrected.  See 
     * {@link Variance} for details on the formula.
     */
    private boolean isBiasCorrected = true;

    /**
     * Constructs a Variance with default (true) <code>isBiasCorrected</code>
     * property.
     */
    public Variance() {
        moment = new SecondMoment();
    }

    /**
     * Constructs a Variance based on an external second moment.
     * 
     * @param m2 the SecondMoment (Third or Fourth moments work
     * here as well.)
     */
    public Variance(final SecondMoment m2) {
        incMoment = false;
        this.moment = m2;
    }
    
    /**
     * Constructs a Variance with the specified <code>isBiasCorrected</code>
     * property
     * 
     * @param isBiasCorrected  setting for bias correction - true means
     * bias will be corrected and is equivalent to using the argumentless
     * constructor
     */
    public Variance(boolean isBiasCorrected) {
        moment = new SecondMoment();
        this.isBiasCorrected = isBiasCorrected;
    }
    
    /**
     * Constructs a Variance with the specified <code>isBiasCorrected</code>
     * property and the supplied external second moment.
     * 
     * @param isBiasCorrected  setting for bias correction - true means
     * bias will be corrected
     * @param m2 the SecondMoment (Third or Fourth moments work
     * here as well.)
     */
    public Variance(boolean isBiasCorrected, SecondMoment m2) {
        incMoment = false;
        this.moment = m2;
        this.isBiasCorrected = isBiasCorrected;      
    }
   
    /**
     * {@inheritDoc}  
     * <p>If all values are available, it is more accurate to use 
     * {@link #evaluate(double[])} rather than adding values one at a time
     * using this method and then executing {@link #getResult}, since
     * <code>evaluate</code> leverages the fact that is has the full 
     * list of values together to execute a two-pass algorithm.  
     * See {@link Variance}.</p>
     */
    public void increment(final double d) {
        if (incMoment) {
            moment.increment(d);
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getResult() {
            if (moment.n == 0) {
                return Double.NaN;
            } else if (moment.n == 1) {
                return 0d;
            } else {
                if (isBiasCorrected) {
                    return moment.m2 / ((double) moment.n - 1d);
                } else {
                    return moment.m2 / ((double) moment.n);
                }
            }
    }

    /**
     * {@inheritDoc}
     */
    public long getN() {
        return moment.getN();
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
    }
    
    /**
     * Returns the variance of the entries in the input array, or 
     * <code>Double.NaN</code> if the array is empty.
     * <p>
     * See {@link Variance} for details on the computing algorithm.</p>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.</p>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * 
     * @param values the input array
     * @return the variance of the values or Double.NaN if length = 0
     * @throws IllegalArgumentException if the array is null
     */
    public double evaluate(final double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("input values array is null");
        }
        return evaluate(values, 0, values.length);
    }

    /**
     * Returns the variance of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * See {@link Variance} for details on the computing algorithm.</p>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.</p>
     * 
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the variance of the values or Double.NaN if length = 0
     * @throws IllegalArgumentException if the array is null or the array index
     *  parameters are not valid
     */
    public double evaluate(final double[] values, final int begin, final int length) {

        double var = Double.NaN;

        if (test(values, begin, length)) {
            clear();
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                Mean mean = new Mean();
                double m = mean.evaluate(values, begin, length);
                var = evaluate(values, m, begin, length);
            }
        }
        return var;
    }
    
    /**
     * Returns the variance of the entries in the specified portion of
     * the input array, using the precomputed mean value.  Returns 
     * <code>Double.NaN</code> if the designated subarray is empty.
     * <p>
     * See {@link Variance} for details on the computing algorithm.</p>
     * <p>
     * The formula used assumes that the supplied mean value is the arithmetic
     * mean of the sample data, not a known population parameter.  This method
     * is supplied only to save computation when the mean has already been
     * computed.</p>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.</p>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * 
     * @param values the input array
     * @param mean the precomputed mean value
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the variance of the values or Double.NaN if length = 0
     * @throws IllegalArgumentException if the array is null or the array index
     *  parameters are not valid
     */
    public double evaluate(final double[] values, final double mean, 
            final int begin, final int length) {
        
        double var = Double.NaN;

        if (test(values, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                double accum = 0.0;
                double dev = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    dev = values[i] - mean;
                    accum += dev * dev;
                    accum2 += dev;
                }
                double len = (double) length;            
                if (isBiasCorrected) {
                    var = (accum - (accum2 * accum2 / len)) / (len - 1.0);
                } else {
                    var = (accum - (accum2 * accum2 / len)) / len;
                }
            }
        }
        return var;
    }
    
    /**
     * Returns the variance of the entries in the input array, using the
     * precomputed mean value.  Returns <code>Double.NaN</code> if the array
     * is empty.
     * <p>
     * See {@link Variance} for details on the computing algorithm.</p>
     * <p>
     * If <code>isBiasCorrected</code> is <code>true</code> the formula used
     * assumes that the supplied mean value is the arithmetic mean of the
     * sample data, not a known population parameter.  If the mean is a known
     * population parameter, or if the "population" version of the variance is
     * desired, set <code>isBiasCorrected</code> to <code>false</code> before
     * invoking this method.</p>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.</p>
     * <p>
     * Does not change the internal state of the statistic.</p>
     * 
     * @param values the input array
     * @param mean the precomputed mean value
     * @return the variance of the values or Double.NaN if the array is empty
     * @throws IllegalArgumentException if the array is null
     */
    public double evaluate(final double[] values, final double mean) {
        return evaluate(values, mean, 0, values.length);
    }

    /**
     * @return Returns the isBiasCorrected.
     */
    public boolean isBiasCorrected() {
        return isBiasCorrected;
    }

    /**
     * @param isBiasCorrected The isBiasCorrected to set.
     */
    public void setBiasCorrected(boolean isBiasCorrected) {
        this.isBiasCorrected = isBiasCorrected;
    }

}
