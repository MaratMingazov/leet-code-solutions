package maratmingazovr.leetcode.tasks.greedy_problems;

// https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/description/
public class BestTimeBuySellStockTask2 {

    public int maxProfit(int[] prices) {
        int n = prices.length;
        int profit = 0;
        for(int i = 1;i < n;i++){
            if(prices[i] > prices[i - 1]){
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }
}
