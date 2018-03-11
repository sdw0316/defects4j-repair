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
package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.util.MathUtils;

/**
 * This class implements the {@link RealVector} interface with a double array.
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class RealVectorImpl implements RealVector, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 7838747548772166404L;

    /** Default format. */
    private static final RealVectorFormat DEFAULT_FORMAT =
        RealVectorFormat.getInstance();

    /** Entries of the vector. */
    protected double data[];

    /**
     * Build a 0-length vector.
     * <p>Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #RealVectorImpl(RealVectorImpl, RealVectorImpl)} constructor
     * or one of the <code>append</code> method ({@link #append(double)}, {@link
     * #append(double[])}, {@link #append(RealVectorImpl)}) to gather data
     * into this vector.</p>
     */
    public RealVectorImpl() {
        data = new double[0];
    }

    /**
     * Construct a (size)-length vector of zeros.
     * @param size size of the vector
     */
    public RealVectorImpl(int size) {
        data = new double[size];
    }

    /**
     * Construct an (size)-length vector with preset values.
     * @param size size of the vector
     * @param preset fill the vector with this scalar value
     */
    public RealVectorImpl(int size, double preset) {
        data = new double[size];
        Arrays.fill(data, preset);
    }

    /**
     * Construct a vector from an array, copying the input array.
     * @param d array of doubles.
     */
    public RealVectorImpl(double[] d) {
        data = d.clone();
    }

    /**
     * Create a new RealVectorImpl using the input array as the underlying
     * data array.
     * <p>If an array is built specially in order to be embedded in a
     * RealVectorImpl and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * @param d data for new vector
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @throws IllegalArgumentException if <code>d</code> is empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #RealVectorImpl(double[])
     */
    public RealVectorImpl(double[] d, boolean copyArray)
        throws NullPointerException, IllegalArgumentException {
        if (d == null) {
            throw new NullPointerException();
        }   
        if (d.length == 0) {
            throw new IllegalArgumentException("Vector must have at least one element."); 
        }
        data = copyArray ? d.clone() :  d;
    }

    /**
     * Construct a vector from part of a array.
     * @param d array of doubles.
     * @param pos position of first entry
     * @param size number of entries to copy
     */
    public RealVectorImpl(double[] d, int pos, int size) {
        if (d.length < pos + size) {
            throw new IllegalArgumentException("Position " + pos + " and size " + size +
                                               " don't fit to the size of the input array " +
                                               d.length);
        }
        data = new double[size];
        System.arraycopy(d, pos, data, 0, size);
    }

    /**
     * Construct a vector from an array.
     * @param d array of Doubles.
     */
    public RealVectorImpl(Double[] d) {
        data = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            data[i] = d[i].doubleValue();
        }
    }

    /**
     * Construct a vector from part of a Double array
     * @param d array of Doubles.
     * @param pos position of first entry
     * @param size number of entries to copy
     */
    public RealVectorImpl(Double[] d, int pos, int size) {
        if (d.length < pos + size) {
            throw new IllegalArgumentException("Position " + pos + " and size " + size +
                                               " don't fit to the size of the input array " +
                                               d.length);
        }
        data = new double[size];
        for (int i = pos; i < pos + size; i++) {
            data[i-pos] = d[i].doubleValue();
        }
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     * @param v vector to copy
     */
    public RealVectorImpl(RealVector v) {
        data = new double[v.getDimension()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = v.getEntry(i);
        }
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     * @param v vector to copy
     */
    public RealVectorImpl(RealVectorImpl v) {
        data = v.data.clone();
    }

    /**
     * Construct a vector from another vector.
     * @param v vector to copy
     * @param deep if true perform a deep copy otherwise perform a shallow copy
     */
    public RealVectorImpl(RealVectorImpl v, boolean deep) {
        data = deep ? v.data.clone() : v.data;
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public RealVectorImpl(RealVectorImpl v1, RealVectorImpl v2) {
        data = new double[v1.data.length + v2.data.length];
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, data, v1.data.length, v2.data.length);
    }

    /** {@inheritDoc} */
    public RealVector copy() {
        return new RealVectorImpl(this, true);
    }

    /** {@inheritDoc} */
    public RealVector add(RealVector v)
    throws IllegalArgumentException {
        try {
            return add((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] + v.getEntry(i);
            }
            return new RealVectorImpl(out);
        }
    }

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public RealVectorImpl add(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] + v.data[i];
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector subtract(RealVector v)
    throws IllegalArgumentException {
        try {
            return subtract((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] - v.getEntry(i);
            }
            return new RealVectorImpl(out);
        }
    }

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public RealVectorImpl subtract(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] - v.data[i];
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapAdd(double d) {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] + d;
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapAddToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] + d;
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSubtract(double d) {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] - d;
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapSubtractToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] - d;
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapMultiply(double d) {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] * d;
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapMultiplyToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] * d;
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapDivide(double d) {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] / d;
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapDivideToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] / d;
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapPow(double d) {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.pow(data[i], d);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapPowToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.pow(data[i], d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapExp() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.exp(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapExpToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.exp(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapExpm1() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.expm1(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapExpm1ToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.expm1(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapLog() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.log(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapLogToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.log(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapLog10() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.log10(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapLog10ToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.log10(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapLog1p() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.log1p(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapLog1pToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.log1p(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCosh() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.cosh(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapCoshToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.cosh(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSinh() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.sinh(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapSinhToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.sinh(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapTanh() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.tanh(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapTanhToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.tanh(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCos() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.cos(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapCosToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.cos(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSin() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.sin(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapSinToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.sin(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapTan() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.tan(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapTanToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.tan(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAcos() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.acos(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapAcosToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.acos(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAsin() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.asin(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapAsinToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.asin(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAtan() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.atan(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapAtanToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.atan(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapInv() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = 1.0 / data[i];
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapInvToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 1.0 / data[i];
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapAbs() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.abs(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapAbsToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.abs(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSqrt() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.sqrt(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapSqrtToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.sqrt(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCbrt() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.cbrt(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapCbrtToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.cbrt(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapCeil() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.ceil(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapCeilToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.ceil(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapFloor() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.floor(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapFloorToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.floor(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapRint() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.rint(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapRintToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.rint(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapSignum() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.signum(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapSignumToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.signum(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector mapUlp() {
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = Math.ulp(data[i]);
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector mapUlpToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.ulp(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector ebeMultiply(RealVector v)
        throws IllegalArgumentException {
        try {
            return ebeMultiply((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] * v.getEntry(i);
            }
            return new RealVectorImpl(out);
        }
    }

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public RealVectorImpl ebeMultiply(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i] * v.data[i];
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector ebeDivide(RealVector v)
        throws IllegalArgumentException {
        try {
            return ebeDivide((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] / v.getEntry(i);
            }
            return new RealVectorImpl(out);
        }
    }

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public RealVectorImpl ebeDivide(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double[] out = new double[data.length];
        for (int i = 0; i < data.length; i++) {
                out[i] = data[i] / v.data[i];
        }
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public double[] getData() {
        return data.clone();
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>Does not make a fresh copy of the underlying data.</p>
     * @return array of entries
     */
    public double[] getDataRef() {
        return data;
    }

    /** {@inheritDoc} */
    public double dotProduct(RealVector v)
        throws IllegalArgumentException {
        try {
            return dotProduct((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v.getEntry(i);
            }
            return dot;
        }
    }

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public double dotProduct(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double dot = 0;
        for (int i = 0; i < data.length; i++) {
            dot += data[i] * v.getEntry(i);
        }
        return dot;
    }

    /** {@inheritDoc} */
    public double getNorm() {
        double sum = 0;
        for (double a : data) {
            sum += a * a;
        }
        return Math.sqrt(sum);
    }

    /** {@inheritDoc} */
    public double getL1Norm() {
        double sum = 0;
        for (double a : data) {
            sum += Math.abs(a);
        }
        return sum;
    }

    /** {@inheritDoc} */
    public double getLInfNorm() {
        double max = 0;
        for (double a : data) {
            max += Math.max(max, Math.abs(a));
        }
        return max;
    }

    /** {@inheritDoc} */
    public double getDistance(RealVector v)
        throws IllegalArgumentException {
        try {
            return getDistance((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i); 
                sum += delta * delta;
            }
            return Math.sqrt(sum);
        }
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * elements differences, or euclidian distance.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(RealVector)
     * @see #getL1Distance(RealVectorImpl)
     * @see #getLInfDistance(RealVectorImpl)
     * @see #getNorm()
     */
    public double getDistance(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double sum = 0;
        for (int i = 0; i < data.length; ++i) {
            final double delta = data[i] - v.data[i];
            sum += delta * delta;
        }
        return Math.sqrt(sum);
    }

    /** {@inheritDoc} */
    public double getL1Distance(RealVector v)
        throws IllegalArgumentException {
        try {
            return getL1Distance((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i); 
                sum += Math.abs(delta);
            }
            return sum;
        }
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(RealVector)
     * @see #getL1Distance(RealVectorImpl)
     * @see #getLInfDistance(RealVectorImpl)
     * @see #getNorm()
     */
    public double getL1Distance(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double sum = 0;
        for (int i = 0; i < data.length; ++i) {
            final double delta = data[i] - v.data[i];
            sum += Math.abs(delta);
        }
        return sum;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(RealVector v)
        throws IllegalArgumentException {
        try {
            return getLInfDistance((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double max = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i); 
                max = Math.max(max, Math.abs(delta));
            }
            return max;
        }
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>&infty;</sub> norm, i.e. the max of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(RealVector)
     * @see #getL1Distance(RealVectorImpl)
     * @see #getLInfDistance(RealVectorImpl)
     * @see #getNorm()
     */
    public double getLInfDistance(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double max = 0;
        for (int i = 0; i < data.length; ++i) {
            final double delta = data[i] - v.data[i];
            max = Math.max(max, Math.abs(delta));
        }
        return max;
    }

    /** {@inheritDoc} */
    public RealVector unitVector() throws ArithmeticException {
        final double norm = getNorm();
        if (norm == 0) {
            throw new ArithmeticException("null norm");
        }
        return mapDivide(getNorm());
    }

    /** {@inheritDoc} */
    public void unitize() throws ArithmeticException {
        final double norm = getNorm();
        if (norm == 0) {
            throw new ArithmeticException("null norm");
        }
        for (int i = 0; i < data.length; i++) {
            data[i] /= norm;
        }
    }

    /** {@inheritDoc} */
    public RealVector projection(RealVector v) {
        return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public RealVectorImpl projection(RealVectorImpl v) {
        return (RealVectorImpl) v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(RealVector v)
        throws IllegalArgumentException {
        try {
            return outerProduct((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            double[][] out = new double[data.length][data.length];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    out[i][j] = data[i] * v.getEntry(j);
                }
            }
            return new RealMatrixImpl(out);
        }
    }

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public RealMatrixImpl outerProduct(RealVectorImpl v)
        throws IllegalArgumentException {
        checkVectorDimensions(v);
        double[][] out = new double[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                out[i][j] = data[i] * v.data[j];
            }
        }
        return new RealMatrixImpl(out);
    }

    /** {@inheritDoc} */
    public double getEntry(int index) throws MatrixIndexException {
        return data[index];
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return data.length;
    }

    /** {@inheritDoc} */
    public RealVector append(RealVector v) {
        try {
            return append((RealVectorImpl) v);
        } catch (ClassCastException cce) {
            return new RealVectorImpl(this,new RealVectorImpl(v));
        }
    }

    /**
     * Construct a vector by appending a vector to this vector.
     * @param v vector to append to this one.
     * @return a new vector
     */
    public RealVectorImpl append(RealVectorImpl v) {
        return new RealVectorImpl(this, v);
    }

    /** {@inheritDoc} */
    public RealVector append(double in) {
        final double[] out = new double[data.length + 1];
        System.arraycopy(data, 0, out, 0, data.length);
        out[data.length] = in;
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector append(double[] in) {
        final double[] out = new double[data.length + in.length];
        System.arraycopy(data, 0, out, 0, data.length);
        System.arraycopy(in, 0, out, data.length, in.length);
        return new RealVectorImpl(out);
    }

    /** {@inheritDoc} */
    public RealVector get(int index, int n) {
        try {
            RealVectorImpl out = new RealVectorImpl(n);
            System.arraycopy(data, index, out.data, 0, n);

            return out;
        } catch (IndexOutOfBoundsException e) {
            throw new MatrixIndexException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void set(int index, double value) {
        try {
            data[index] = value;
        } catch (IndexOutOfBoundsException e) {
            throw new MatrixIndexException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void set(int index, RealVector v) {
        try {
            try {
                set(index, (RealVectorImpl) v);
            } catch (ClassCastException cce) {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    data[i] = v.getEntry(i-index);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MatrixIndexException(e.getMessage());
        }
    }

    /**
     * Set a set of consecutive elements.
     * 
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     */
    public void set(int index, RealVectorImpl v)
        throws MatrixIndexException {
        try {
            System.arraycopy(v.data, 0, data, index, v.data.length);
        } catch (IndexOutOfBoundsException e) {
            throw new MatrixIndexException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void set(double value) {
        Arrays.fill(data, value);
    }

    /** {@inheritDoc} */
    public double[] toArray(){
        return data.clone();
    }

    /** {@inheritDoc} */
    public String toString(){
        return DEFAULT_FORMAT.format(this);
    }

    /**
     * Check if instance and specified vectors have the same dimension.
     * @param v vector to compare instance with
     * @exception IllegalArgumentException if the vectors do not
     * have the same dimension
     */
    public void checkVectorDimensions(RealVector v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     * 
     * @param n expected dimension.
     * @exception IllegalArgumentException if the dimension is
     * inconsistent with vector size
     */
    public void checkVectorDimensions(int n)
        throws IllegalArgumentException {
        if (data.length != n) {
            throw new IllegalArgumentException("vector dimension is " + data.length +
                                               ", not " + n + " as expected");
        }
    }

    /**
     * Returns true if any coordinate of this vector is NaN; false otherwise
     * @return  true if any coordinate of this vector is NaN; false otherwise
     */
    public boolean isNaN() {
        for (double v : data) {
            if (Double.isNaN(v)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if any coordinate of this vector is infinite and none are NaN;
     * false otherwise
     * @return  true if any coordinate of this vector is infinite and none are NaN;
     * false otherwise
     */
    public boolean isInfinite() {

        if (isNaN()) {
            return false;
        }

        for (double v : data) {
            if (Double.isInfinite(v)) {
                return true;
            }
        }

        return false;

    }
    
    /**
     * Test for the equality of two real vectors.
     * <p>
     * If all coordinates of two real vectors are exactly the same, and none are
     * <code>Double.NaN</code>, the two real vectors are considered to be equal.
     * </p>
     * <p>
     * <code>NaN</code> coordinates are considered to affect globally the vector
     * and be equals to each other - i.e, if either (or all) coordinates of the
     * real vector are equal to <code>Double.NaN</code>, the real vector is equal to
     * a vector with all <code>Double.NaN</code> coordinates.
     * </p>
     *
     * @param other Object to test for equality to this
     * @return true if two 3D vector objects are equal, false if
     *         object is null, not an instance of Vector3D, or
     *         not equal to this Vector3D instance
     * 
     */
    public boolean equals(Object other) {

      if (this == other) { 
        return true;
      }

      if (other == null) {
        return false;
      }

      try {

          RealVector rhs = (RealVector) other;
          if (data.length != rhs.getDimension()) {
              return false;
          }

          if (rhs.isNaN()) {
              return this.isNaN();
          }

          for (int i = 0; i < data.length; ++i) {
              if (Double.doubleToRawLongBits(data[i]) !=
                  Double.doubleToRawLongBits(rhs.getEntry(i))) {
                  return false;
              }
          }
          return true;

      } catch (ClassCastException ex) {
          // ignore exception
          return false;
      }

    }
    
    /**
     * Get a hashCode for the real vector.
     * <p>All NaN values have the same hash code.</p>
     * @return a hash code value for this object
     */
    public int hashCode() {
        if (isNaN()) {
            return 9;
        }
        return MathUtils.hash(data);
    }

}
