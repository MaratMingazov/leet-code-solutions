package maratmingazovr.leetcode.tasks.greedy_problems;

// https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/description/
public class BestTimeBuySellStockTransactionFeeTask {

    public int maxProfit(int[] prices, int fee) {
        return dp(prices, fee, 0, 0, new Integer[prices.length][2]);
    }

    public int dp(int[] prices, int fee, int index, int buyFlag, Integer[][] memo) {
        if (index == prices.length) {
            return 0;
        }
        if (memo[index][buyFlag] != null) {
            return memo[index][buyFlag];
        }

        if (buyFlag == 0) {
            int buy = dp(prices, fee, index+1, 1, memo) - prices[index];
            int doNothing = dp(prices, fee, index+1, 0, memo);
            memo[index][buyFlag] = Math.max(buy, doNothing);
        } else {
            int sell = dp(prices, fee, index+1, 0, memo) + prices[index] - fee;
            int doNothing = dp(prices, fee, index+1, 1, memo);
            memo[index][buyFlag] = Math.max(sell, doNothing);
        }
        return memo[index][buyFlag];
    }

}
