package org.pzks;

import org.pzks.parsers.ExpressionParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    public static void main(String[] args) throws Exception {
        String testString1 = "+!,11  .   442)(a23-+(tix2u39u9nyu08%, 93ux h3028i9i8  988.23.31 9 $!()p  (a,   ,)#    (*U@!)! #!@ @ #@*5. 81.8  .   4.2  34 -     )6)b(b  (/a/2325)(";
        String testString2 = "&a+-=b*(c*cos(t-a*x)-d*sin(t+a*x)/(4.&81*k-q*t))/(d*cos(t+a*y/f1(5.616*x-t))+c*sin(t-a*y*((u-v*i)))";
        String testString3 = "1+(a+b&)    /   ";
        String testString4 = "a+(b*cos(x/y+2)";
        String testString5 = "a+(b*cos(&% %% ()(    ) * (  ))x/y-   +2";
        String testString6 = "(((())))";
        String testString7 = "(a+";
        String testString8 = "(a+b)b(d-c)";
        String testString9 = "a+()";
        String testString10 = "b((";
        String testString11 = "  s + b  (    ,,  ,  +b,c,)";
        String testString12 = "  s + b (   c + 4   + (   f -   3)";
        String testString13 = "b(,d)";
        String testString14 = "b(b  (/a/2325)(";
        String testString15 = "p   (   +   g    )";
        String testString16 = "p((),())";
        String testString17 = "p   ((), s   )";
        String testString18 = "p(p())";
        String test = "c6+11*442/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31+9-p(a)/(g4*U)*5*81.8-4.2*34-z2)*6)*b(b(v7/a/2325))";
        new ExpressionParser().parse(test, true, true, true);
//        System.out.println(testString1.charAt(136));
//        System.out.println    ("jk");
        // 6, 10

        List<Integer> errorPositions = List.of(4, 4, 7);

        // Find the maximum position to determine the number of lines
//        int maxPosition = Collections.max(errorPositions);
//        System.out.println(maxPosition);
    }
}