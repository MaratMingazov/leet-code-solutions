package maratmingazovr.leetcode.tasks.constraint_satisfaction.queens;

import lombok.val;
import maratmingazovr.leetcode.constraint_satisfaction.CSP;
import maratmingazovr.leetcode.constraint_satisfaction.Constraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueensConstraint extends Constraint<Verticals, Horizontals> {

    private List<Verticals> verticals;

    public QueensConstraint(List<Verticals> verticals) {
        super(verticals);
        this.verticals = verticals;
    }




    @Override
    public boolean satisfied(Map<Verticals, Horizontals> assignment) {
        for (Map.Entry<Verticals, Horizontals> item : assignment.entrySet()) {
            // q1c = queen 1 column, q1r = queen 1 row
            val q1c = item.getKey();
            val q1r = item.getValue();

            for (Verticals vertical : verticals) {
                if (q1c.equals(vertical) ){
                    continue;
                }
                if (assignment.containsKey(vertical)) {
                    val horizontal = assignment.get(vertical);
                    // same row?
                    if (q1r.equals(horizontal)) {
                        return false;
                    }
                    // same diagonal?
                    if (Math.abs(q1r.value - horizontal.value) == Math.abs(q1c.value - vertical.value)) {
                        return false;
                    }
                }
            }
        }
        return true; // no conflict
    }

    public static void main(String[] args) {
        List<Verticals> verticals = List.of(Verticals.A, Verticals.B, Verticals.C, Verticals.D, Verticals.F, Verticals.E, Verticals.G, Verticals.H);
        Map<Verticals, List<Horizontals>> horizontals = new HashMap<>();
        for (Verticals vertical : verticals) {
            horizontals.put(vertical, List.of(Horizontals._1, Horizontals._2, Horizontals._3, Horizontals._4, Horizontals._5, Horizontals._6, Horizontals._7, Horizontals._8));
        }
        CSP<Verticals, Horizontals> csp = new CSP<>(verticals, horizontals);
        csp.addConstraint(new QueensConstraint(verticals));
        Map<Verticals, Horizontals> solution = csp.backtrackingSearch();
        if (solution == null) {
            System.out.println("No solution found!");
        } else {
            System.out.println(solution);
        }
    }
}
