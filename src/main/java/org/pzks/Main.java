package org.pzks;

import org.pzks.parsers.ExpressionParser;
import org.pzks.utils.ProgramArgsProcessor;

public class Main {

    public static void main(String[] args) throws Exception {
        String s = "+!11  .   442)(a23-+(tix2u39u9nyu08 93ux h3028i9i8  988.23.31 9 $!()#    (*U@!)! #!@ @ #@*5. 81.8  .   4.2  34 -     )6)b(b(/a/2325)(";
        String s1 = "a+b*(c*cos(t-a*x)-d*sin(t+a*x)/(4.81*k-q*t))/(d*cos(t+a*y/f1(5.616*x-t))+c*sin(t-a*y*(u-v*i)))";
        //        System.out.println(s);
//        System.out.println(Arrays.stream(s.split("")).toList());
        System.out.println(s.split("")[132]);
        System.out.println(s1.split("")[27]);
//        new ExpressionParser().parse("+!11  .   442)(a23-+(tix2u39u9nyu08 93ux h3028i9i8  988.23.31 9 $!()#    (*U@!)! #!@ @ #@*5. 81.8  .   4.2  34 -     )6)b(b(/a/2325)(", true, true);
        new ExpressionParser().parse(s1, true, true);
//        new ExpressionParser().parse("a+b*(c*cos(t-a*x)-d*sin(t+a*x)/(4.81*k-q*t))", true, true);
    }

//    public static void main(String[] args) throws Exception {
//        ProgramArgsProcessor programArgsProcessor = new ProgramArgsProcessor(args);
//        new ExpressionParser().parse(
//                programArgsProcessor.getExpression(),
//                programArgsProcessor.shouldBuildTree(),
//                programArgsProcessor.shouldCheckForErrors()
//        );
//    }
}