package maratmingazovr.leetcode.tasks.arrays;

import java.util.PriorityQueue;
import java.util.Queue;

public class SlidingWindowMedianTask {

    public double[] medianSlidingWindow(int[] nums, int k) {
        Queue<Double> left = new PriorityQueue<>((a, b) -> Double.compare(b, a));
        Queue<Double> right = new PriorityQueue<>((a,b) -> Double.compare(a,b));

        if (k > nums.length) {
            return new double[0];
        }
        double[] medians = new double[nums.length - k + 1];

        for (int i = 0; i < nums.length; i++) {
            add(left, right, nums[i]);
            if (i + 1 >= k) {
                medians[i-k+1] = getMedian(left, right);
                remove(left, right, nums[i-k+1]);
            }
        }
        return medians;
    }

    public void add(Queue<Double> left, Queue<Double> right, double value) {
        if (left.size() == 0 || value > left.peek()) {
            right.add(value);
        } else {
            left.add(value);
        }
        rebalance(left,right);
    }

    public void rebalance(Queue<Double> left, Queue<Double> right) {
        if (left.size() > right.size() + 1) {
            right.add(left.poll());
        } else if (right.size() > left.size() + 1) {
            left.add(right.poll());
        }
    }

    public double getMedian(Queue<Double> left, Queue<Double> right) {
        if (left.size() == right.size()) {
            return (left.peek() + right.peek()) / 2.0;
        } else if (left.size() > right.size()) {
            return left.peek();
        } else {
            return right.peek();
        }
    }

    public void remove(Queue<Double> left, Queue<Double> right, double value) {
        if (left.size() > 0 && value <= left.peek()) {
            left.remove(value);
        } else {
            right.remove(value);
        }
        rebalance(left, right);
    }
}
