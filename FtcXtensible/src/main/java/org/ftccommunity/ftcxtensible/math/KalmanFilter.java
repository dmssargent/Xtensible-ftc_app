/*
 * Copyright © 2015 David Sargent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and  to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ftccommunity.ftcxtensible.math;

import Jama.Matrix;

/**
 * This work is licensed under a Creative Commons Attribution 3.0 License,
 * the Creative Commons overrides the MIT
 *
 * @author Ahmed Abdelkader, David Sargent
 */
// TODO: update
public final class KalmanFilter {
    private Matrix X;
    private Matrix X0;
    private Matrix F;
    private Matrix B;
    private Matrix U;
    private Matrix Q;
    private Matrix H;
    private Matrix R;
    private Matrix P;
    private Matrix P0;

    public KalmanFilter(int variables) {
        setX(new Matrix(variables, variables));
        setX0(new Matrix(variables, variables));

        F = new Matrix(variables, variables);
        setB(new Matrix(variables, variables));
        setU(new Matrix(variables, variables));
        setQ(new Matrix(variables, variables));

        setH(new Matrix(variables, variables));
        setR(new Matrix(variables, variables));

        setP(new Matrix(variables, variables));
        setP0(new Matrix(variables, variables));

    }

    public void predict() {
        setX0(F.times(getX()).plus(getB().times(getU())));
        setP0(F.times(getP()).times(F.transpose()).plus(getQ()));
    }

    public void correct(Matrix Z) {
        Matrix S = getH().times(getP0()).times(getH().transpose()).plus(getR());

        Matrix K = getP0().times(getH().transpose()).times(S.inverse());

        setX(getX0().plus(K.times(Z.minus(getH().times(getX0())))));

        Matrix I = Matrix.identity(getP0().getRowDimension(), getP0().getColumnDimension());
        setP((I.minus(K.times(getH()))).times(getP0()));
    }

    public Matrix getX() {
        return X;
    }

    public void setX(Matrix x) {
        X = x;
    }

    public Matrix getX0() {
        return X0;
    }

    public void setX0(Matrix x0) {
        X0 = x0;
    }

    public Matrix getB() {
        return B;
    }

    public void setB(Matrix b) {
        B = b;
    }

    public Matrix getU() {
        return U;
    }

    public void setU(Matrix u) {
        U = u;
    }

    public Matrix getQ() {
        return Q;
    }

    public void setQ(Matrix q) {
        Q = q;
    }

    public Matrix getH() {
        return H;
    }

    public void setH(Matrix h) {
        H = h;
    }

    public Matrix getR() {
        return R;
    }

    public void setR(Matrix r) {
        R = r;
    }

    public Matrix getP() {
        return P;
    }

    public void setP(Matrix p) {
        P = p;
    }

    public Matrix getP0() {
        return P0;
    }

    public void setP0(Matrix p0) {
        P0 = p0;
    }

}