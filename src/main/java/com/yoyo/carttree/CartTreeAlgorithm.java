package com.yoyo.carttree;

import Jama.Matrix;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoyo.common.DataSetLoadUtils;
import com.yoyo.common.MatrixUtils;

import java.util.*;

public class CartTreeAlgorithm {

    /**
     * 对数据集做最好的划分
     *
     * @param dataset 数据集
     * @param tolS    方差容忍的最小误差
     * @param tolN    数据集的行数可划分的最小行数
     */
    private static Map<String, Object> chooseBestSplit(Matrix dataset, int tolS, int tolN) {

        Map<String, Object> bestSplit = new HashMap<>();
        double variance = MatrixUtils.calculateVariance(dataset);
        double bestVariance = variance;
        int bestIndex = 0;
        double bestValue = 0.0;

        // 1. 对数据集的每个特征（列）
        for (int col = 0; col < dataset.getColumnDimension() - 1; col++) {
            Set<Double> attrs = MatrixUtils.set(dataset, col);

            // 1.1 对每个特征的每个属性
            for (double attr : attrs) {
                // 1.1.1 把给该列数据切分为两部分mat1, mat2,即在该列上其值小于阈值的为mat0, 否则为mat1
                final MatrixUtils.MatrixPair matrixPair = MatrixUtils.split(dataset, col, attr);

                // 计算mat1,mat2的方差
                Matrix mat0 = matrixPair.getMat0();
                Matrix mat1 = matrixPair.getMat1();

                // （1）mat0,mat1=null, 是指当属性是最小值或最大值时
                // （2）mat0的行数、mat1的行数很小时，表示数据已经不可分了
                if (mat0 == null || mat1 == null || mat0.getRowDimension() < tolN || mat1.getRowDimension() < tolN) {
                    continue;
                }

                // 选择所有方差中最小的哪个，记录其列、属性值
                final double curVariance = MatrixUtils.calculateVariance(mat0) + MatrixUtils.calculateVariance(mat1);
                if (curVariance < bestVariance) {
                    bestVariance = curVariance;
                    bestIndex = col;
                    bestValue = attr;
                }
            }
        }

        // 表示已经没什么好分的了
        if (Math.abs(bestVariance - variance) < tolS) {
            bestSplit.put("bestIndex", "none");
            bestSplit.put("spVal", MatrixUtils.calculateMean(dataset));
            return bestSplit;
        }

        final MatrixUtils.MatrixPair bestMatrixPair = MatrixUtils.split(dataset, bestIndex, bestValue);
        Matrix mat0 = bestMatrixPair.getMat0();
        Matrix mat1 = bestMatrixPair.getMat1();
        bestSplit.put("bestIndex", bestIndex);
        bestSplit.put("spVal", bestValue);
        return bestSplit;
    }

    /**
     * 递归构造cart树
     *
     * @param dataSet 数据集
     * @param tolS    方差容忍的最小误差
     * @param tolN    数据集的行数可划分的最小行数
     * @return
     */
    public static Map<String, Object> buildCartTree(Matrix dataSet, int tolS, int tolN) {
        Map<String, Object> tree = new LinkedHashMap<>();
        final Map<String, Object> bestSplit = chooseBestSplit(dataSet, tolS, tolN);
        // 是叶子节点了
        if (Objects.equals(bestSplit.get("bestIndex").toString(), "none")) {
            bestSplit.remove("bestIndex");
            return bestSplit;
        }

        int bestIndex = (int) bestSplit.get("bestIndex");
        double bestValue = (double) bestSplit.get("spVal");
        final MatrixUtils.MatrixPair matrixPair = MatrixUtils.split(dataSet, bestIndex, bestValue);

        // 根节点
        tree.put("spInd", bestIndex);
        tree.put("spVal", bestValue);

        // 构建左子树
        tree.put("left", buildCartTree(matrixPair.getMat0(), tolS, tolN));
        // 构建右字数
        tree.put("right", buildCartTree(matrixPair.getMat1(), tolS, tolN));
        return tree;
    }

    public static void test1() throws Exception {
        double[][] dataSet = DataSetLoadUtils.loadDataSet("/ex00.txt", "\t", 2);
        Matrix matrix = new Matrix(dataSet);

        final Map<String, Object> tree = buildCartTree(matrix, 1, 4);

        ObjectMapper objectMapper = new ObjectMapper();
        final String value = objectMapper.writeValueAsString(tree);

        System.out.println(value);
    }

    public static void test2() throws Exception {
        double[][] dataSet = DataSetLoadUtils.loadDataSet("/ex0.txt", "\t", 3);
        Matrix matrix = new Matrix(dataSet);

        final Map<String, Object> tree = buildCartTree(matrix, 1, 4);

        ObjectMapper objectMapper = new ObjectMapper();
        final String value = objectMapper.writeValueAsString(tree);

        System.out.println(value);
    }

    public static void main(String[] args) throws Exception {
        test1();
        test2();
    }
}
