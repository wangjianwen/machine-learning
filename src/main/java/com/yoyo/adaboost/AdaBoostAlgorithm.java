package com.yoyo.adaboost;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaBoostAlgorithm {

    /**
     * 用阈值threshold, 根据数据集的某一列给数据集分类
     * @param dataset 数据集
     * @param dim 数据集的某一列
     * @param threshold 阈值
     * @param thresholdIneq 小于（lt） 或 非小于()
     * @return 分类标签
     */
    private static double[] stumpClassify(Matrix dataset, int dim, double threshold, String thresholdIneq) {
        double[] retArray = new double[dataset.getRowDimension()];
        for (int i = 0; i < retArray.length; i++) {
            retArray[i] = 1.0;
        }

        Matrix columMatrix = dataset.getMatrix(0, dataset.getRowDimension() - 1, dim, dim);
        for (int i = 0; i < columMatrix.getRowDimension(); i++) {
            if (thresholdIneq.equals("lt")) {
                if (columMatrix.get(i, 0) <= threshold) {
                    retArray[i] = -1.0;
                }
            } else {
                if (columMatrix.get(i, 0) > threshold) {
                    retArray[i] = -1.0;
                }
            }
        }
        return retArray;
    }

    /**
     * 构建单次最优决策树
     * @param dataset 数据集
     * @param classLabels 分类标签
     * @param D 权值矩阵
     * @return 单次最优决策树划分
     */
    private static Map<String, Object> buildStump(final Matrix dataset, final double[] classLabels, final Matrix D) {
        Map<String, Object> bestStump = new HashMap<>();
        int totalStep = 10;
        String[] threshHoldIneqs = new String[]{"lt", "gt"};

        double minErrorRation = 10000.0;
        for (int dim = 0; dim < dataset.getColumnDimension(); dim++) {
            double min = MatrixUtils.min(dataset, dim);
            double max = MatrixUtils.max(dataset, dim);
            double step = (max - min) / totalStep;


            for (int j = -1; j < totalStep; j++) {
                double curThreshold = min + j * step;
                for (String threshHoldIneq : threshHoldIneqs) {
                    double[] classifyResult = stumpClassify(dataset, dim, curThreshold, threshHoldIneq);
                    Matrix err = MatrixUtils.getErrMatrix(classLabels, classifyResult);

                    double errorRatio = D.transpose().times(err).get(0, 0);

                    if (minErrorRation > errorRatio) {
                        bestStump.put("dim", dim);
                        bestStump.put("threshold", curThreshold);
                        bestStump.put("ineq", threshHoldIneq);
                        bestStump.put("classifyResult", classifyResult);
                        minErrorRation = errorRatio;
                    }
                }
            }
        }
        bestStump.put("errorRation", minErrorRation);
        return bestStump;
    }

    /**
     * 对数据集进行adaBoost训练，得到多个决策树集合
     * @param dataset 数据集
     * @param classLabels 分类标签
     * @param iterTimes 最大迭代次数
     * @return 多个决策树集合
     */
    public static List<Map<String, Object>> adaBoostTraining(final Matrix dataset, final double[] classLabels, int iterTimes) {
        List<Map<String, Object>> classifyList = new ArrayList<>();

        double[][] d = {{0.2}, {0.2}, {0.2}, {0.2}, {0.2}};
        Matrix D = new Matrix(d);

        int j = 0;
        double[] aggClassEst = new double[classLabels.length];
        while (j++ < iterTimes) {

            Map<String, Object> stump = buildStump(dataset, classLabels, D);
            double e = (double) stump.get("errorRation");
            double alpha = 0.5 * Math.log((1.0 - e) / e);
            stump.put("alpha", alpha);
            classifyList.add(stump);

            double[] previctResult = (double[]) stump.get("classifyResult");
            reCalculateMatrixD(D, alpha, classLabels, previctResult);

            // 计算累计
            for(int i= 0; i < classLabels.length; i++){
                aggClassEst[i] += alpha * previctResult[i];
            }

            double[] flag = new double[classLabels.length];
            for(int i = 0; i < classLabels.length; i++){
                flag[i] = Math.signum(aggClassEst[i]);
            }

            Matrix err = MatrixUtils.getErrMatrix(classLabels, flag);
            double errorRatio = D.transpose().times(err).get(0, 0);

            if(errorRatio == 0.0){
                break;
            }
        }
        return classifyList;
    }


    /**
     * 重新计算D
     * @param D
     * @param alpha
     * @param classLabels 分类标签
     * @param predictResult 预测结果
     */
    private static void reCalculateMatrixD(final Matrix D, double alpha, final double[] classLabels, final double[] predictResult) {
        for (int i = 0; i < predictResult.length; i++) {
            double expon = (-1.0) * predictResult[i] * classLabels[i] * alpha;
            double original = D.get(i, 0);
            D.set(i, 0, original * Math.exp(expon));
        }

        final double sum = MatrixUtils.sum(D, 0);
        for (int i = 0; i < predictResult.length; i++) {

            double original = D.get(i, 0);
            D.set(i, 0, original / sum);
        }
    }

    /**
     * ada boost 分类
     * @param datToClass 待分类的数据
     * @param classifyList 多个决策树
     * @return 分类结果
     */
    private static double[] adaBoostClassify(final Matrix datToClass, final List<Map<String, Object>> classifyList){
        double[] result = new double[datToClass.getRowDimension()];
        for (int i = 0; i < classifyList.size(); i++){
            final Map<String, Object> classify = classifyList.get(i);

            int dim = (int) classify.get("dim");
            double threshold = (double)classify.get("threshold");
            String ineq = classify.get("ineq").toString();
            final double[] classifyResult = stumpClassify(datToClass, dim, threshold, ineq);

            for(int j = 0; j < result.length; j++){
                result[j] += classifyResult[j];
            }
        }

        double[] flag = new double[result.length];
        for(int j = 0; j < result.length; j++){
            flag[j] = Math.signum(result[j]);
        }
        return flag;
    }


    public static void main(String[] args) {
        double[][] array = {{1.0, 2.1}, {2.0, 1.1}, {1.3, 1.0}, {1.0, 1.0}, {2.0, 1.0}};
        Matrix dataset = new Matrix(array);

        double[] classLabels = {1.0, 1.0, -1.0, -1.0, 1.0};
        List<Map<String, Object>> list = adaBoostTraining(dataset, classLabels, 9);
        double[][] xx = {{0.8, 5}, {5, 0.9}};
        final double[] classify = adaBoostClassify(new Matrix(xx), list);

        for(int i = 0; i < classify.length; i++){
            System.out.println(classify[i]);
        }
    }
}
