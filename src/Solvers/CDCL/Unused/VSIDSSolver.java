package Solvers.CDCL.Unused;

import DataStructures.*;
import Solvers.CDCL.ChaffSolver;

public class VSIDSSolver extends ChaffSolver {

    public VSIDSSolver(Clauses clauses, int literalsCount) {
        super(clauses, literalsCount);
    }

    @Override
    protected Variable pickBranchingVariable() {
        Variable branchVariable = stateGraph.getNextVSIDSUnassignedVariable(level);
        //// System.out.println("+ Decision (unassigned): " + branchVariable); ////

        return branchVariable;
    }

}
