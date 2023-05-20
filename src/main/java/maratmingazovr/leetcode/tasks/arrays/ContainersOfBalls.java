package maratmingazovr.leetcode.tasks.arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://www.hackerrank.com/challenges/organizing-containers-of-balls
public class ContainersOfBalls {

    public String organizingContainers(List<List<Integer>> container) {
        List<Integer> containersCapacity = new ArrayList<>();
        for (List<Integer> currentContainer: container) {
            int sum = 0;
            for (Integer balls: currentContainer) {
                sum += balls;
            }
            containersCapacity.add(sum);
        }

        Map<Integer, Integer> ballsMap = new HashMap<>();
        for (List<Integer> currenctContainer : container) {
            for (int j = 0; j < currenctContainer.size(); j++) {
                int balls = currenctContainer.get(j);
                if (ballsMap.containsKey(j)) {
                    ballsMap.put(j, ballsMap.get(j) + balls);
                } else {
                    ballsMap.put(j, balls);
                }
            }
        }
        List<Integer> balls = new ArrayList<>(ballsMap.values());

        return balls.containsAll(containersCapacity)
                ? "Possible"
                : "Impossible";
    }

}
