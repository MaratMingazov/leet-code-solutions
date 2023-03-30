package maratmingazovr.leetcode.tasks.integers;

public class ReverseIntegerTask {

    public int reverseInteger(int x) {
        String value = Integer.toString(x);
        if (x < 0) {
            value = value.substring(1);
        }
        StringBuilder reversString = new StringBuilder();
        for (int i = value.length()-1; i >= 0; i--) {
            reversString.append(value.charAt(i));
        }

        int reverseInt = 0;
        try {
            reverseInt = Integer.parseInt(reversString.toString());
        } catch (Exception e) {

        }
        if (x < 0) {
            reverseInt = -1 * reverseInt;
        }
        return reverseInt;
    }

    public int reverseInteger2(int x) {
        long finalNum = 0;
        while(x!=0) {
            int a = x%10;
            finalNum += a;
            finalNum = finalNum * 10;
            x = x / 10;
        }
        finalNum = finalNum / 10;
        if (finalNum > Integer.MAX_VALUE || finalNum < Integer.MIN_VALUE) {
            return 0;
        }
        if (x < 0) {
            return (int) (-1*finalNum);
        }
        return (int) finalNum;

    }
}
