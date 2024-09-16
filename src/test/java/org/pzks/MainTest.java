package org.pzks;

import org.pzks.parsers.ExpressionParser;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    public static void main(String[] args) throws Exception {
        String testString1 = "+!11  .   442)(a23-+(tix2u39u9nyu08 93ux h3028i9i8  988.23.31 9 $!()#    (*U@!)! #!@ @ #@*5. 81.8  .   4.2  34 -     )6)b(b(/a/2325)(";
        String testString2 = "&a+-=b*(c*cos(t-a*x)-d*sin(t+a*x)/(4.&81*k-q*t))/(d*cos(t+a*y/f1(5.616*x-t))+c*sin(t-a*y*((u-v*i)))";
        String testString3 = "1+(a+b&)    /   ";
        String testString4 = "a+(b*cos(x/y+2)";
        String testString5 = "a+(b*cos(&% %% ()(    ) * (  ))x/y-   +2";
        String testString6 = "(((())8))";
        String testString7 = "(a+";
        String testString8 = "(a+b)b(d-c)";
        String testString9 = "a+()";
        String testString10 = "b((";
        new ExpressionParser().parse(testString10, true, true, true);
    }
}