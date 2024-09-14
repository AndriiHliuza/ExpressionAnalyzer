package org.pzks.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CharSequenceNode {
    private final List<String> processedValue = new ArrayList<>();
    private boolean isProcessed;
    private final String nonProcessedValue;

    public CharSequenceNode(String valueToProcess) {
        nonProcessedValue = valueToProcess;
    }

    public List<String> getProcessedValue() {
        return processedValue;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public String getNonProcessedValue() {
        return nonProcessedValue;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharSequenceNode that = (CharSequenceNode) o;
        return isProcessed == that.isProcessed && Objects.equals(processedValue, that.processedValue) && Objects.equals(nonProcessedValue, that.nonProcessedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processedValue, isProcessed, nonProcessedValue);
    }

    @Override
    public String toString() {
        return "CharSequenceNode{" +
                "processedValue=" + processedValue +
                ", isProcessed=" + isProcessed +
                ", nonProcessedValue='" + nonProcessedValue + '\'' +
                '}';
    }
}
