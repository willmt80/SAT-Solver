package Solvers.CDCL;

import DataStructures.*;

public class VSIDSSolver extends ChaffSolver {

    public VSIDSSolver(Clauses clauses, int literalsCount) {
        super(clauses, literalsCount);
    }

    @Override
    protected Variable pickBranchingVariable() {
        Variable branchingVariable = stateGraph.getNextVSIDSUnassignedVariable(level);
        //// System.out.println("+ Decision (unassigned): " + branchingVariable); ////

        return branchingVariable;
    }

}
