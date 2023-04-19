package maratmingazovr.leetcode.tasks.greedy_problems;

// https://leetcode.com/problems/best-time-to-buy-and-sell-stock/description/
public class BestTimeBuySellStockTask {

    public int maxProfit(int[] prices) {

        if (prices.length < 2) {
            return 0;
        }
        int total = 0;
        int buyIndex = 0;

        for (int sellIndex = 0; sellIndex < prices.length; sellIndex++) {
            if (prices[sellIndex] < prices[buyIndex]) {
                buyIndex = sellIndex;
            }
            total = Math.max(total, (prices[sellIndex] - prices[buyIndex]));
        }

        return total;
    }
}
