package com.yoyo.common;

import com.yoyo.graddescent.Algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataSetLoadUtils {

    /**
     * 加载数据集
     * @param fileName
     * @param regex
     * @param dim
     * @return 数据集
     */
    public static double[][] loadDataSet(String fileName, String regex, int dim) {
        int no = 0;
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(DataSetLoadUtils.class.getResource(fileName).getPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!lines.isEmpty()) {
            double[][] dataset = new double[lines.size()][dim];
            for (int i = 0; i < lines.size(); i++) {
                String[] temp = lines.get(i).split(regex);
                for(int j = 0; j < dim; j++){
                    dataset[i][j] = Double.valueOf(temp[j]);
                }
            }
            return dataset;
        }
        return null;
    }
}
