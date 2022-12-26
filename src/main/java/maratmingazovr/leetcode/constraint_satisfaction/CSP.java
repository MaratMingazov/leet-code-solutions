package maratmingazovr.leetcode.constraint_satisfaction;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSP<V, D>  {

    @NonNull
    private List<V> variables;
    @NonNull
    private Map<V, List<D>> domains;
    @NonNull
    private Map<V, List<Constraint<V, D>>> constraints = new HashMap<>();

    public CSP(@NonNull List<V> variables,
               @NonNull Map<V, List<D>> domains) {
        this.variables = variables;
        this.domains = domains;
        for (V variable : variables) {
            constraints.put(variable, new ArrayList<>());
            if (!domains.containsKey(variable)) {
                throw new IllegalArgumentException("Every variable should have a domain assigned to it.");
            }
        }
    }

    public void addConstraint(Constraint<V, D> constraint) {
        for (V variable : constraint.variables) {
            if (!variables.contains(variable)) {
                throw new IllegalArgumentException("Variable in constraint not in CSP");
            }
            constraints.get(variable).add(constraint);
        }
    }

    // Check if the value assignment is consistent by checking all constraints
    // for the given variable against it
    public boolean consistent(V variable, Map<V, D> assignment) {
        for (Constraint<V, D> constraint : constraints.get(variable)) {
            if (!constraint.satisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

    // helper for backtrackingSearch when nothing known yet
    public Map<V, D> backtrackingSearch() {
        return backtrackingSearch(new HashMap<>());
    }

    public Map<V, D> backtrackingSearch(Map<V, D> assignment) {
        // assignment is complete if every variable is assigned (our base case)
        if (assignment.size() == variables.size()) {
            return assignment;
        }
        // get the first variable in the CSP but not in the assignment
        V unassigned = variables.stream().filter(v -> !assignment.containsKey(v)).findFirst().get();
        // get the every possible domain value of the first unassigned variable
        for (D value : domains.get(unassigned)) {
            // shallow copy of assignment that we can change
            Map<V, D> localAssignment = new HashMap<>(assignment);
            localAssignment.put(unassigned, value);
            // if we're still consistent, we recurse (continue)
            if (consistent(unassigned, localAssignment)) {
                Map<V, D> result = backtrackingSearch(localAssignment);
                // if we didn't find the result, we will end up backtracking
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
