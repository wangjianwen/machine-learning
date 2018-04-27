package com.yoyo.graddescent;

import com.yoyo.common.DataSetLoadUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 梯度下降算法，根据训练数据集（位于data.csv），预测模型：y=a+bx， 求a, b 的值
 */
public class Algorithm {

    private static final String FILE_NAME = "/data.csv";



    /**
     * 计算预测模型函数的值
     * @param theta0 theta0
     * @param theta1
     * @param x 参数
     * @return
     */
    private static double calculatePredictionFunctionVal(double theta0, double theta1, double x) {
        return theta0 + theta1 * x;
    }

    /**
     * 计算梯度
     * @param dataset 数据集
     * @param theta0 参数0
     * @param theta1 参数1
     * @param isTheta0
     * @return
     */
    private static double calculateGrad(double[][] dataset, double theta0, double theta1, boolean isTheta0) {
        double sum = 0.0;
        double N = (double) dataset.length;

        for (int i = 0; i < dataset.length; i++) {
            double x = dataset[i][0];
            double y = dataset[i][1];
            double h = calculatePredictionFunctionVal(theta0, theta1, x);
            double xi = 1.0;
            if (!isTheta0) xi = x;
            sum += (1.0 / N * (h - y) * xi);
        }
        return sum;
    }

    /**
     * 执行梯度下降
     * @param dataset 数据集
     */
    private static void doGradDescent(double[][] dataset) {
        double theta0 = 0.0;
        double theta1 = 0.0;
        double alpha = 0.0001;
        double e = 0.0000001;
        double curTheta0 = 1111.0;
        double curTheta1 = 1111.0;

        int i = 0;
        while (Math.abs(theta0 - curTheta0) >= e || Math.abs(theta1 - curTheta1) >= e) {
            curTheta0 = theta0;
            curTheta1 = theta1;

            double grad0 = calculateGrad(dataset, theta0, theta1, true);
            double grad1 = calculateGrad(dataset, theta0, theta1, false);

            theta0 = theta0 - alpha * grad0;
            theta1 = theta1 - alpha * grad1;
        }
        System.out.println(theta0 + "," + theta1);
    }

    public static void main(String[] args) {
        double[][] dataSet = DataSetLoadUtils.loadDataSet(FILE_NAME, ",", 2);
        doGradDescent(dataSet);
    }
}
