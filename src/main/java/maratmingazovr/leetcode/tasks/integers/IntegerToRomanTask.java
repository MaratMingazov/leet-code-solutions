package maratmingazovr.leetcode.tasks.integers;

//https://leetcode.com/problems/integer-to-roman
public class IntegerToRomanTask {

    public String intToRoman(int num) {
        String result = "";
        int thousands = num / 1000;
        for (int i = 0; i < thousands; i ++) {
            result +="M";
        }
        num = num % 1000;
        int hundreds = num / 100;
        if (hundreds == 9) {
            result += "CM";
        } else if (hundreds == 4) {
            result += "CD";
        } else if (hundreds >= 5) {
            result += "D";
            hundreds -= 5;
            for (int i = 0; i < hundreds; i ++) {
                result +="C";
            }
        } else {
            for (int i = 0; i < hundreds; i ++) {
                result +="C";
            }
        }
        num = num % 100;
        int decades = num / 10;
        if (decades == 9) {
            result +="XC";
        } else if (decades == 4) {
            result +="XL";
        } else if (decades >= 5) {
            result += "L";
            decades -= 5;
            for (int i = 0; i < decades; i ++) {
                result +="X";
            }
        } else {
            for (int i = 0; i < decades; i ++) {
                result +="X";
            }
        }
        num = num % 10;
        int digits = num;
        if (digits == 9) {
            result +="IX";
        } else if (digits == 4) {
            result +="IV";
        } else if (digits >= 5) {
            result += "V";
            digits -= 5;
            for (int i = 0; i < digits; i ++) {
                result +="I";
            }
        } else {
            for (int i = 0; i < digits; i ++) {
                result +="I";
            }
        }
        return result;
    }

}
