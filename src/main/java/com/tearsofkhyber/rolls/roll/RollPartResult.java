package com.tearsofkhyber.rolls.roll;


import java.util.List;

public final class RollPartResult {

    private final Roll.RollPart rollPart;
    private final List<Integer> individualResults;
    private final int result;

    public RollPartResult(Roll.RollPart rollPart, List<Integer> results) {
        this.rollPart = rollPart;
        this.individualResults = results;
        this.result = results.stream().reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        return "(" + individualResults
                .stream()
                .map(result -> Integer.toString(result))
                .reduce((a, b) -> a + (b.startsWith("-") ? "" : "+") + b)
                .orElse("Invalid roll.")
                + ")";
    }

    public Roll.RollPart getRollPart() {
        return rollPart;
    }

    public Integer getResult() {
        return result;
    }
}
