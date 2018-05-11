package Solvers.CDCL;

import DataStructures.*;

import java.util.*;

public class ResolutionSolver extends AllClauseSolver {

    private HashMap<Clause, ArrayList<Clause>> resolutions;
    private HashSet<Clause> learntAntecedents;
    private Clause emptyClause;

    public ResolutionSolver(Clauses clauses, int literalsCount) {
        super(clauses, literalsCount);
        resolutions = new HashMap<>();
        learntAntecedents = new HashSet<>();
        emptyClause = null;
    }

    @Override
    public HashMap<String, Boolean> solve() {
        resetSolver();

        performGeneralUnitPropagation();

        if (stateGraph.isConflicted()) {
            /** CONFLICT **/
            System.out.println("Fail to propagate before entering loop."); ////
            return null;
        }

        level = 0;

        while (!stateGraph.isAllAssigned()) {
            Variable decision = pickBranchingVariable();
            if (decision == null) {
                System.out.println("Fail to make a decision."); ////
                return null;
            }

            level += 1;
            //// System.out.println("Level: " + (level - 1) + " -> " + level); ////

            stateGraph.addDecision(decision, level);
            //// System.out.println("Decision: " + decision); ////

            performDecisionUnitPropagation(decision);

            if (!stateGraph.isConflicted()) {
                continue;
            }

            Clause conflict = stateGraph.getConflictClause();
            Integer proposedLevel = analyzeConflict(conflict);
            if (proposedLevel == null) {
                /** CONFLICT **/
                System.out.println("Fail to propose a backtrack level."); ////
                return null;
            }

            backtrack(proposedLevel);

            level += 1;

            performGeneralUnitPropagation();

            if (stateGraph.isConflicted()) {
                boolean isCertified = certifyUnsatisfiable(learntAntecedents);
                if (isCertified) {
                    outputProof();
                }
                System.out.println("Fail to propagate after backtracking from conflict."); ////
                return null;
            }
        }

        System.out.println("Satisfiable: " + stateGraph.evaluate(formula)); ////
        return stateGraph.getAssignment();
    }

    @Override
    protected Integer analyzeConflict(Clause conflictClause) {
        Node<Variable> conflictNode = stateGraph.getConflictNode();
        if (conflictClause == null || conflictNode == null) {
            return null;
        }
        Integer conflictLevel = conflictNode.getValue().getLevel();

        Clause learntClause = new Clause(conflictClause.toArray());

        learntAntecedents.add(conflictClause);

        while(!stateGraph.isAtUniqueImplicationPoint(learntClause, conflictLevel)) {
            Clause oldClause = new Clause(learntClause.toUnsortedArray());
            Clause antecedentClause = stateGraph.getNextAntecedentClause(learntClause, conflictLevel);

            //// System.out.println("Resolving: " + learntClause + " with " + antecedentClause); ////
            learntClause = applyResolution(learntClause, antecedentClause);
            //// System.out.println(" ... into: " + learntClause); ////

            if (!learntClause.equals(oldClause)) {
                learntAntecedents.add(antecedentClause);
            }
        }

        learntClauses.add(learntClause);

        Integer highestLevel = stateGraph.getHighestLevel(learntClause, conflictLevel);

        if (highestLevel == null) {
            //// System.out.println("Fail to obtain highest level for backtrack."); ////
            return null;
        }

        Integer backtrackLevel = highestLevel == 0 ? 0 : highestLevel - 1;
        //// System.out.println("Proposed backtrack level: " + backtrackLevel); ////

        return backtrackLevel < 0 ? null : backtrackLevel;
    }

    @Override
    protected void resetSolver() {
        super.resetSolver();
        resolutions = new HashMap<>();
        learntAntecedents = new HashSet<>();
        emptyClause = null;
    }

    private boolean certifyUnsatisfiable(HashSet<Clause> seedClauses) {
        int threshold = 5;
        ArrayList<Clause> resolutionList = new ArrayList<>(seedClauses);

        while(!resolutionList.isEmpty()) {

            System.out.println(resolutionList); ////

            HashSet<Clause> derivedSet = new HashSet<>();
            int listSize = resolutionList.size();

            for (int i = 0; i < listSize; i++) {
                Clause clauseA = resolutionList.get(i);
                if (clauseA.getLiterals().size() > threshold) {
                    continue;
                }

                for (int j = 0; j < listSize; j++) {
                    Clause clauseB = resolutionList.get(j);
                    if (clauseB.getLiterals().size() > threshold) {
                        continue;
                    }

                    Clause resolvedClause = applyResolution(clauseA, clauseB);
                    if (resolvedClause.getLiterals().isEmpty()) {
                        emptyClause = resolvedClause;
                        return true;
                    }
                    if (resolvedClause.getLiterals().size() <= threshold && !resolutions.containsKey(resolvedClause)) {
                        ArrayList<Clause> workingSet = new ArrayList<>();
                        workingSet.add(clauseA);
                        workingSet.add(clauseB);
                        resolutions.put(resolvedClause, workingSet);
                        derivedSet.add(resolvedClause);
                    }
                }
            }

            HashSet<Clause> newSet = new HashSet<>(resolutionList);
            newSet.addAll(derivedSet);

            if (newSet.size() == listSize) {
                return false;
            }

            resolutionList = new ArrayList<>(newSet);
        }

        return false;
    }

    private void outputProof() {
        if (emptyClause == null) {
            return;
        }

        ArrayList<String> clausesOutput = new ArrayList<>();
        ArrayList<String> workingsOutput = new ArrayList<>();

        HashSet<Clause> clauses = new HashSet<>();
        HashSet<Clause> currentClauses = new HashSet<>();
        currentClauses.add(emptyClause);

        while (!currentClauses.isEmpty()) {
            // 1. Create derived workings list
            // 2. Create removed clauses set
            // 3. Create complex clauses set
            // 4. For each clause in current clauses set, check for resolution reference
                // a. If reference exists, parse and append to derived workings list
                // b. Add all 3 clauses to clause log set
                // c. Add clause to removed clause set
                // d. Add complex derived working clauses to complex clause set
            // 5. Insert derived workings to start of workings output list
            // 6. Remove removed clauses from clauses set
            // 7. Add complex clauses to clauses set
        }

        // For all clauses in clauses set, populate clauses output

        // If in file output mode, write clause and working output to file
        // Otherwise, print clause and working output to console log

        System.out.println("Certified UNSAT!");
    }

}