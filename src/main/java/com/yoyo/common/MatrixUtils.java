package com.yoyo.common;

import Jama.Matrix;

import java.util.HashSet;
import java.util.Set;

public class MatrixUtils {

    private MatrixUtils(){

    }

    public static Matrix getErrMatrix(double[] classLabels, double[] classifyResult) {
        double[][] array = new double[classLabels.length][1];
        for (int i = 0; i < classLabels.length; i++) {
            if (classifyResult[i] == classLabels[i]) {
                array[i][0] = 0.0;
            } else {
                array[i][0] = 1.0;
            }
        }
        return new Matrix(array);
    }

    public static Matrix mat(double[] values){
        double[][] array = new double[values.length][1];

        for(int i = 0; i < values.length; i++){
            array[i][0] = values[i];
        }
        return new Matrix(array);
    }


    public static double min(Matrix dataset, int dim) {
        double min = 100000000.0;
        for (int i = 0; i < dataset.getRowDimension(); i++) {
            if (min > dataset.get(i, dim)) {
                min = dataset.get(i, dim);
            }
        }
        return min;
    }

    public static double max(Matrix dataset, int dim) {
        double max = -100000000.0;
        for (int i = 0; i < dataset.getRowDimension(); i++) {
            if (max < dataset.get(i, dim)) {
                max = dataset.get(i, dim);
            }
        }
        return max;
    }

    public static double sum(Matrix dataset, int dim) {

        double sum = 0.0;
        for (int i = 0; i < dataset.getRowDimension(); i++) {
            sum += dataset.get(i, dim);
        }
        return sum;
    }

    public static Set<Double> set(Matrix dataset, int dim) {

        Set<Double> set = new HashSet<>();
        for (int i = 0; i < dataset.getRowDimension(); i++) {
            set.add(dataset.get(i, dim));
        }
        return set;
    }

    public static MatrixPair split(Matrix dataset, int dim, double threshold){
        int numOfValueLessThanThreshold = 0;
        for(int i = 0; i < dataset.getRowDimension(); i++){
            if(dataset.get(i, dim) > threshold){
                numOfValueLessThanThreshold++;
            }
        }
        double[][] array1 = new double[numOfValueLessThanThreshold][dataset.getColumnDimension()];
        double[][] array2 = new double[dataset.getRowDimension()-numOfValueLessThanThreshold][dataset.getColumnDimension()];

        int row1 = 0;
        int row2 = 0;
        for(int i = 0; i < dataset.getRowDimension(); i++){
            if(dataset.get(i, dim) > threshold){
                for(int j = 0; j < dataset.getColumnDimension(); j++){
                    array1[row1][j] = dataset.get(i, j);
                }
                row1++;
            } else {
                for(int j = 0; j < dataset.getColumnDimension(); j++){
                    array2[row2][j] = dataset.get(i, j);
                }
                row2++;
            }
        }

        if(array1.length == 0){
            Matrix matrix1 = new Matrix(array2);
            return new MatrixPair(null, matrix1);
        } else if(array2.length == 0){
            Matrix matrix0 = new Matrix(array1);
            return new MatrixPair(matrix0, null);
        }
        Matrix matrix0 = new Matrix(array1);
        Matrix matrix1 = new Matrix(array2);
        return new MatrixPair(matrix0, matrix1);

    }

    public static double calculateMean(Matrix matrix){
        int index = matrix.getColumnDimension() - 1;

        double sum = 0.0;
        for(int i = 0; i < matrix.getRowDimension(); i++){
            sum += matrix.get(i, index);
        }
        return sum / matrix.getRowDimension();
    }

    public static double calculateVariance(Matrix matrix){
        int index = matrix.getColumnDimension() - 1;
        double mean = calculateMean(matrix);

        double variance = 0.0;
        for(int i = 0; i < matrix.getRowDimension(); i++){
            variance += Math.pow((matrix.get(i, index) - mean), 2);
        }
        return variance;
    }

    public static class MatrixPair {
        private final Matrix mat0;
        private final Matrix mat1;

        public MatrixPair(Matrix mat0, Matrix mat1){
            this.mat0 = mat0;
            this.mat1 = mat1;
        }
        public Matrix getMat0() {
            return mat0;
        }

        public Matrix getMat1() {
            return mat1;
        }

    }
}
