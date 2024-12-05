package org.pzks;

import org.pzks.parsers.ExpressionProcessor;
import org.pzks.settings.Configuration;
import org.pzks.settings.GlobalSettings;
import org.pzks.utils.Statistics;
import org.pzks.settings.args.processor.ProgramArgsProcessor;

public class Main {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        ProgramArgsProcessor programArgsProcessor = new ProgramArgsProcessor(args);

        if (programArgsProcessor.isValidUsage()) {
            GlobalSettings.configure(new Configuration(programArgsProcessor));
            ExpressionProcessor.process(programArgsProcessor.getExpression());
        }
        long endTime = System.nanoTime();

        Statistics.displayTime(startTime, endTime);
    }
}