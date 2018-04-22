package com.yoyo.adaboost;

import Jama.Matrix;

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
}
