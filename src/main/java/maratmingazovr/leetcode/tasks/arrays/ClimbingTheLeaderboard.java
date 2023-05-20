package maratmingazovr.leetcode.tasks.arrays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://www.hackerrank.com/challenges/climbing-the-leaderboard
public class ClimbingTheLeaderboard {

    /*
     * Complete the 'climbingLeaderboard' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. INTEGER_ARRAY ranked
     *  2. INTEGER_ARRAY player
     */

    public List<Integer> climbingLeaderboard(List<Integer> ranked, List<Integer> player) {
        // Write your code here
        List<Integer> result = new ArrayList<>();
        if (ranked.isEmpty()) {
            return result;
        }

        Map<Integer, Integer> rankedMap = new HashMap<>();

        int rankNumber = 0;
        for (Integer rank: ranked) {
            if (!rankedMap.containsKey(rank)) {
                rankNumber++;
                rankedMap.put(rank, rankNumber);
            }
        }
        List<Integer> indexes = new ArrayList<>();
        indexes.add(0);
        Map<Integer, Integer> playerMap = new HashMap<>();
        for (int i = player.size()-1; i>=0; i--) {
            int playerValue = player.get(i);
            if (playerMap.containsKey(playerValue)) {
                result.add(playerMap.get(playerValue));
            } else {
                int playerRank = calculatePlayerRank(playerValue, ranked, rankedMap, indexes);
                playerMap.put(playerValue, playerRank);
                result.add(playerRank);
            }
        }
        Collections.reverse(result);
        return result;

    }

    public int calculatePlayerRank(int playerValue,
                                          List<Integer> ranked,
                                          Map<Integer, Integer> rankedMap,
                                          List<Integer> indexes) {
        for (int i = indexes.get(indexes.size()-1); i < ranked.size(); i++) {
            int rank = ranked.get(i);
            if (playerValue >= rank) {
                indexes.add(i);
                return rankedMap.get(rank);
            }
        }
        int lastIndex = ranked.size()-1;
        int lastRank = ranked.get(lastIndex);
        indexes.add(lastIndex);
        return rankedMap.get(lastRank) + 1;
    }
}
