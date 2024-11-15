package org.pzks;

import org.pzks.parsers.ExpressionParser;
import org.pzks.utils.Configuration;
import org.pzks.utils.GlobalSettings;
import org.pzks.utils.Statistics;
import org.pzks.utils.args.processor.ProgramArgsProcessor;

public class Main {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        ProgramArgsProcessor programArgsProcessor = new ProgramArgsProcessor(args);

        if (programArgsProcessor.isValidUsage()) {
            GlobalSettings.configure(new Configuration(programArgsProcessor));
            ExpressionParser.parse(programArgsProcessor.getExpression());
        }
        long endTime = System.nanoTime();

        Statistics.displayTime(startTime, endTime);
    }
}