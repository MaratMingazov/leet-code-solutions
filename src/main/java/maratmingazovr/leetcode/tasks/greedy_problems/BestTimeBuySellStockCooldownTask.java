package maratmingazovr.leetcode.tasks.greedy_problems;

// https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/description/
public class BestTimeBuySellStockCooldownTask {

    public int maxProfit(int[] prices) {
        return dp(prices, 0, 0, 0, new Integer[prices.length][2][2]);

    }

    public int dp(int[] prices, int index, int buyFlag, int coolDown, Integer[][][] memo) {
        if (index == prices.length) {
            return 0;
        }

        if (memo[index][buyFlag][coolDown] != null) {
            return memo[index][buyFlag][coolDown];
        }

        if (coolDown == 1) {
            memo[index][buyFlag][coolDown] = dp(prices, index+1, buyFlag, 0, memo);
        } else {
            if (buyFlag == 0) {
                int buy = dp(prices, index + 1, 1, 0, memo) - prices[index];
                int doNothing = dp(prices, index+1, 0, 0, memo);
                memo[index][buyFlag][coolDown] = Math.max(buy, doNothing);
            } else {
                int sell = dp(prices, index + 1, 0, 1, memo) + prices[index];
                int doNothing = dp(prices, index + 1, 1, 0, memo);
                memo[index][buyFlag][coolDown] = Math.max(sell, doNothing);
            }
        }
        return memo[index][buyFlag][coolDown];

    }

}
