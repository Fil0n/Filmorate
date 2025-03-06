package ru.yandex.practicum.filmrate.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BinarySlopeOne {
    private Map<Long, Map<Long, Double>> matrix = new HashMap<>();
    private Map<Long, Set<Long>> inputData;

    public BinarySlopeOne(Map<Long, Set<Long>> inputData) {
        this.inputData = inputData;
        buildDifferenceMatrix();
    }

    private void buildDifferenceMatrix() {
        for (Long product1 : inputData.keySet()) {
            matrix.putIfAbsent(product1, new HashMap<>());

            for (Long product2 : inputData.keySet()) {
                if (product1.equals(product2)) continue;

                Set<Long> scoreSet1 = inputData.get(product1);
                Set<Long> scoreSet2 = inputData.get(product2);

                Set<Long> intersection = new HashSet<>(scoreSet1);
                intersection.retainAll(scoreSet2);

                double diff = (double) intersection.size() / scoreSet1.size();
                matrix.get(product1).put(product2, diff);
            }
        }
    }

    public Map<Long, Double> predict(Long userid) {
        Set<Long> productIds = new HashSet<>();
        // получаем все фильмы, которые оценил пользователь
        for (Map.Entry<Long, Set<Long>> entry : inputData.entrySet()) {
            if (entry.getValue().contains(userid)) {
                productIds.add(entry.getKey());
            }
        }

        Map<Long, Double> predictions = new HashMap<>();

        for (Long productId : productIds) {
            if (!matrix.containsKey(productId)) continue;

            for (Map.Entry<Long, Double> entry : matrix.get(productId).entrySet()) {
                Long recommendedProductId = entry.getKey();
                double diff = entry.getValue();

                if (!productIds.contains(recommendedProductId)) {
                    predictions.put(recommendedProductId, predictions.getOrDefault(recommendedProductId, 0.0) + diff);
                }
            }
        }
        return predictions;
    }
}
