package com.yoyo.common;

import Jama.Matrix;
import org.junit.Assert;
import org.junit.Test;

public class MatrixUtilsTest {
    @Test
    public void testSplit() {
        double[][] array = {{1.0, 0.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0, 0.0}, { 0.0, 0.0, 1.0, 0.0}, { 0.0, 0.0, 0.0, 1.0}};

        Matrix matrix = new Matrix(array);
        final MatrixUtils.MatrixPair matrixPair = MatrixUtils.split(matrix, 1, 0.5);

        Assert.assertEquals(matrixPair.getMat0().getRowDimension(), 1);
        Assert.assertEquals(matrixPair.getMat1().getRowDimension(), 3);
    }

    @Test
    public void testCalculateVariance() {
        double[][] array = {{1.0, 0.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0, 0.0}, { 0.0, 0.0, 1.0, 0.0}, { 0.0, 0.0, 0.0, 1.0}};

        Matrix matrix = new Matrix(array);
        final double variance = MatrixUtils.calculateVariance(matrix);

        System.out.println(variance);

    }
}
