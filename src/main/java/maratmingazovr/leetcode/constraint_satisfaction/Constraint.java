package maratmingazovr.leetcode.constraint_satisfaction;

import java.util.List;
import java.util.Map;

// V is the variable type, and D is the domain type
public abstract class Constraint<V, D> {

    // the variables that the constraint is between
    // Переменные, для которых существует ограничение
    protected List<V> variables;

    public Constraint(List<V> variables) {
        this.variables = variables;
    }

    // Метод проверяет выполняются ли ограничения
    // must be overridden by subclasses
    public abstract boolean satisfied(Map<V, D> assignment);
}
