package org.pzks;

import org.pzks.parsers.ExpressionParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    public static void main(String[] args) throws Exception {
        String testString1 = "((+!,11  .   442)(a23-+(tix2u39u9nyu08%, 93ux h3028i9i8  988.23.31 9 $!()p  (a,   ,)#    (*U@!)! #!@ @ #@*5. 81.8  .   4.2  34 -     )6)b(b  (/a/2325)(";
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
        String testString19 = "(a+(s))";
        String testString20 = "-5+(-6 + (-4) + p(  (4+(-5)) , (6 + 4), 6))";
        String testString21 = "-5+6*9/0";
        String testString22 = "-5/6*9/0";
        String testString23 = "1*0 * 5 + 0 / (5-5 ) * a";
        String testString24 = "0 / (5-5)";
        String testString25 = "0+a";
        String testString26 = "7 / (0)";
        String testString27 = "7 + a";
        String testString28 = "a + 7";
        String testString29 = "((1))*d"; // positive 1 +1 (+1) works fine with not number
        String testString29_1 = "d*((1))"; // positive 1 +1 (+1) works fine with not numbers
        String testString30 = "4*(-1)";
        String testString31 = "(-3)*5";
        String testString32 = "(-0)*d"; // no
        String testString33 = "d*(-7)";
        String testString34 = "(-7)*d";
        String testString35 = "b(8/(0))";
        String testString36 = "d/(8*0)";

        String testString37 = "(+0)*d"; // + and - zero works fine
        String testString38 = "d*((-0))"; // + and - zero works fine


        String testString39 = "(-1)*d()"; // + and - one works fine
        String testString40 = "d*(-1)"; // + and - one works fine
        String testString41 = "4*d()*(-1)"; // + and - one works fine


        String test = "c6+11*442/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31+9-p(a)/(g4*U)*5*81.8-4.2*34-z2)*6)*b(b(6/(0)/2325))";
        String test2 = "c6+0*442/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31+9-p(a)/(g4*U)*5*81.8-4.2*34-z2)*6)*b(b(6/7/2325))";
        String test3 = "0*c6*0+11*442/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31/0+9-p(a)/(g4*U)*5*81.8-4.2*34-z2/(5-5/((4-4)+1)))*6)*b(b(6/7/2325))";
        String test4 = "-5*5+(+s)";
        String test5 = "5+(+s)";
        String test6 = "c6+442/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31+9-p(a)/(g4*U)*5*81.8-4.2*34-z2)*6)*b(b(6/7/2325,a+d, a*b/c*d/e*f, a*b/c/d/e*f))+c+a*g+(d-45)+(3-d+(d+e+v/w)+4*g)+a/b/c/d/e/f+a-b-c-d-e-f";
        String test7 = "c + a * g + (d - 45) + (3 - d + (d+e+v/w) + 4 * g)";
        String test8 = "p(b(a,d),f)";
        String test9 = "p(a,d)";
        new ExpressionParser().parse(test6, true, true, true, true, true);


//        int a = 2;
//        System.out.println(-a);
//        List<String> s = new ArrayList<>();
//        s.add("1");
//        s.add("2");
//        s.add("3");
//        s.add("4");
//        s.add("5");
//        s.add("6");
//        System.out.println(s);
//        for (int i = 0; i < s.size(); i++) {
//            if (i == 2) {
//                s.subList(i, i + 2).clear();
//                s.add(i, "9");
//            }
//        }

//        System.out.println(s);
//        String a = "-";
//        switch (a) {
//            case String operationValue when operationValue.matches("[+-]") -> {
//                System.out.println("+-");
//            }
//            case String operationValue when operationValue.equals("*") -> {
//                System.out.println("*");
//            }
//            case String operationValue when operationValue.equals("/") -> {
//                System.out.println("/");
//            }
//            default -> throw new IllegalStateException("Unexpected value: ");
//        }
        // !!!!!!!!! variable division on 0 Simplified expression: c6+4862/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/1028.23-p(a)/(g4*U)*266.2-z2)*6)*b(b(v7/0))

        //        System.out.println(testString1.charAt(136));
//        System.out.println    ("jk");

//        List<String> list = new ArrayList<>();
//        list.add("A");
//        list.add("B");
//        list.add("C");
//        list.add("D");
//        list.add("E");
//
//        // Specify start (inclusive) and end (exclusive) indexes
//        int startIndex = 1;
//        int endIndex = 4;
//
//        List<String> l = list.subList(startIndex, endIndex);
//        System.out.println(l);
//        l.clear();
//
//        System.out.println(list); // Output will be [A, E]
//        System.out.println(list.size());
//
//        list.add(1, "FFF");
//        System.out.println(list);

//        List<Integer> errorPositions = List.of(4, 4, 7);

        // Find the maximum position to determine the number of lines
//        int maxPosition = Collections.max(errorPositions);
//        System.out.println(maxPosition);

//        int a = 4;
//        System.out.println(-5+(-a));
//        System.out.println(5/0);

//        System.out.println(Double.parseDouble("-0") == 0);
//        Double.parseDouble("0");
//        System.out.println(0.0/0.0);
    }
}