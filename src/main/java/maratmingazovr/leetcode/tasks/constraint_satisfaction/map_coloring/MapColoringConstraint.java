package maratmingazovr.leetcode.tasks.constraint_satisfaction.map_coloring;

import lombok.NonNull;
import maratmingazovr.leetcode.constraint_satisfaction.CSP;
import maratmingazovr.leetcode.constraint_satisfaction.Constraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapColoringConstraint extends Constraint<Region, Color> {

    @NonNull
    private Region region1;

    @NonNull
    private Region region2;

    public MapColoringConstraint(@NonNull Region region1, @NonNull Region region2) {
        super(List.of(region1, region2));
        this.region1 = region1;
        this.region2 = region2;
    }

    @Override
    public boolean satisfied(Map<Region, Color> assignment) {
        // if either place is not in the assignment, then it is not
        // yet possible for their colors to be conflicting
        if (!assignment.containsKey(region1) || !assignment.containsKey(region2)) {
            return true;
        }
        // check the color assigned to place1 is not the same as the
        // color assigned to place2
        return !assignment.get(region1).equals(assignment.get(region2));
    }

    public static void main(String[] args) {
        List<Region> variables = List.of(Region.NEW_SOUTH_WALES,
                                         Region.NORTHERN_TERRITORY,
                                         Region.QUEENSLAND,
                                         Region.SOUTH_AUSTRALIA,
                                         Region.TASMANIA,
                                         Region.VICTORIA,
                                         Region.WESTERN_AUSTRALIA);
        Map<Region, List<Color>> domains = new HashMap<>();
        for (Region variable : variables) {
            domains.put(variable, List.of(Color.RED, Color.BLUE, Color.GREEN));
        }
        CSP<Region, Color> csp = new CSP<>(variables, domains);
        csp.addConstraint(new MapColoringConstraint(Region.WESTERN_AUSTRALIA, Region.NORTHERN_TERRITORY));
        csp.addConstraint(new MapColoringConstraint(Region.WESTERN_AUSTRALIA, Region.SOUTH_AUSTRALIA));
        csp.addConstraint(new MapColoringConstraint(Region.SOUTH_AUSTRALIA, Region.NORTHERN_TERRITORY));
        csp.addConstraint(new MapColoringConstraint(Region.QUEENSLAND, Region.NORTHERN_TERRITORY));
        csp.addConstraint(new MapColoringConstraint(Region.QUEENSLAND, Region.SOUTH_AUSTRALIA));
        csp.addConstraint(new MapColoringConstraint(Region.QUEENSLAND, Region.NEW_SOUTH_WALES));
        csp.addConstraint(new MapColoringConstraint(Region.NEW_SOUTH_WALES, Region.SOUTH_AUSTRALIA));
        csp.addConstraint(new MapColoringConstraint(Region.VICTORIA, Region.SOUTH_AUSTRALIA));
        csp.addConstraint(new MapColoringConstraint(Region.VICTORIA, Region.NEW_SOUTH_WALES));
        csp.addConstraint(new MapColoringConstraint(Region.VICTORIA, Region.TASMANIA));
        Map<Region, Color> solution = csp.backtrackingSearch();
        if (solution == null) {
            System.out.println("No solution found!");
        } else {
            System.out.println(solution);
        }
    }
}
