package org.pzks;

import org.pzks.parsers.ExpressionProcessor;
import org.pzks.settings.Configuration;
import org.pzks.settings.GlobalSettings;
import org.pzks.utils.Statistics;
import org.pzks.settings.args.processor.BoolArg;

import java.util.List;

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
        String test6 = "-c6+442/(-a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31+9-p(a)/(g4*U)*5*81.8-4.2*34-z2)*6)*b(b(6/7/2325,a+d, a*b/c*d/e*f, a*b/c/d/e*f))+c+a*g+(d-45)+(3-d+(d+e+v/w)+4*g)+a/b/c/d/e/f+a-b-c-d-e-f";
        String test7 = "c + a * g + (d - 45) + (3 - d + (d+e+v/w) + 4 * g)";
        String test8 = "p(b(a,d),f)";
        String test9 = "p(a,d)";
        String test10 = "42/(a23-(tix2u39u9nyu08-93/ux*h3028i9i8/988.23+31+9-p(a)/(g4*U)*5*81.8-4.2*34-z2)*6)";
        String test11 = "a+(-b)";
        String test12= "p()";
        String test13= "d+(-1/a)";
        String test14= "(d+a)";
        String test15= "d*(-5)";
        String test16= "-c+a/s/(-5)/d+(-c+d)-e-f+(-g-h-4-j)+a*b";
        String test17= "f+(-g-h-i-j)";
        String test18= "-(-c)";
        String test19= "(((a+b*((((c/d)))))))";
        String test20= "d+(-1/(-c))";
        String test21= "1/(1/(1/(a+b)))";
        String test22= "1/(1/(1/(a+b))) +p()";
        String test23= "     ";
        String test24= "d+((-c)-r)";
        String test25= "e+a*b+f/(-(c+d))+e";
        String test26= "a-b";
        String test27= "a/(-d)*b";
        String test28= "-c-d";
        String test29= "-5/d";
        String test30= "-(-i)/1.0 + 0 - 0*k*h + 2 - 4.8/2 + 1*e/2";
        String test31= "a*2/0 + b/(b+b*0-1*b) - 1/(c*2*4.76*(1-2+1))";
        String test32= "i-5+3";
        String test33= "-(-i)/1.0 + 0 - 0*k*h + 2 - 4.8/2 + 1*e/2";
        String test34= "b+0";
        String test35= "a/b/c/d/(1/e)/f/g/h/k";
        String test36= "a*b - b*c - c*d - a*c*(b-d/e/f/g) - (g - h) - (i-j)";
        String test37= "a*b + (((-d)/(b*f)*c)*d)-g*h";
        String test38= "a*b + (((-d)/b)*d)-g*h";
        String test39= "(((-1)*b)*c)*k+f*g";
        String test40= "(-1)*k+f*g";
        String test41= "a*2/0 + b/(b+b*0-1*b) - 1/(c*2*4.76*(1-2+1)) + (30*f/f+5)";
        String test42= "a-((b-c-d)-(e-f)-g)-h";
        String test43= "a-b-c-d-e-f-g-h";
        String test44= "a+(b+c+d+(e+f)+g)+h";
        String test45= "a+b+c-d*f";
        String test46= "a-b+c+d+e-f+g-h";
        String test47= "a+c+d+e+g+(-1)*b+(-1)*f+(-1)*h";
        String test48= "a-b-c";
        String test49= "a+c+d+e+g-b-f-h";
        String test50= "a*b - b*c - c*d - a*c*(b-d/e/f/g) - (g - h) - (i-j)";
        String test51= "d-5/d";
        String test52= "a+(b+c+d+(e+f)+g)+h";
        String test53= "a*b - b*c - c*d - a*c*(b-d/e/f/g) - (g - h) - (i-j)";
        String test54= "a*(-1)*b";
        String test55= "exp(sin(a+b/2-pi)+a*cos(a*pi+b*pi/3-w+k*t)-5+log(2.72)/T-1)+2048+a+b*c+log(t-1)-2*log(Q)-8*d/dt*exp(t/2+H)-sin(a)/cos(a)";
        String test56= "a + b*c + d + e*f*g + h*i + j*(k + L + m*(n-p*q+r) - s*t)";
        String test57= "-8*d/dt*exp(t/2+H)-sin(a)/cos(a)-2*log(Q)+b*c+exp(log(2.72)/T+a*cos(b*pi/3+k*t+a*pi-w)+sin(b/2+a-pi)-6)+2048+a+log(t-1)";
        String test58= "-8*d/dt*exp(t/2+H)+b*c-2*log(Q)-sin(a)/cos(a)+exp(a*cos(b*pi/3+a*pi+k*t-w)+log(2.72)/T+sin(b/2+a-pi)-6)+2048+a+log(t-1)";
        String test59= "a*b+c*d+e*f";
        String test60= "-a/k*b-a/k*c+f(s, d+1+g*d)+b*c*f*j+c*i*j*d+f(s,d + 1+g*d)*f";

        // a * (b+c) + b*c + c*d

        // a * b + c * (a+b) + c * d
        // a * b + c * (a+b+d)


        String test61= "a*(b-2)+c*(b-2)";
        String test62= "a/b";

        String test63= "-a/k*b-a/k*c+b*c*f*j+c*i*j*d";
        String test64= "(b-2)+(a+c)";
        String test65= "(b*d+f*b+b*g)+(a+c)";
        String test66= "f*(b*d+f*b+b*g)+f*(a+c)";


        String test67= "a-b*k+b*t-f*f*5.9+f*q+g*f*5.9-g*q-f/(d+q-w)-g/(d+q-w)";

        String test68= "(a-5)+(a-5)*6";
        String test69= "d+(-5)";

        String test70= "f*f*5.9+f*q";
        String test71= "b*t-b*f*f*5.9+b*f*q+b*g*f*5.9-b*g*q";
        String test72= "a-b*k+b*t-b*f*f*5.9+b*f*q+b*g*f*5.9-b*g*q-b*w/p+b*y*m/p-b*y/p-x*x/(d+q-w)+3*x/(d+q-w)+3*x/(d+q-w)+3*3/(d+q-w)";

        String test73= "a-b*k+b*t-b*f*f*5.9+b*f*q+b*g*f*5.9-b*g*q-b*w/p+b*y*m/p-b*y/p-x*x/(d+q-w)+3*x/(d+q-w)+3*x/(d+q-w)+3*3/(d+q-w)";
        String test74= "b*g*q-b*w/p+b*y*m/p-b*y/p-x*x/(d+q-w)+3*x/(d+q-w)+3*x/(d+q-w)+3*3/(d+q-w)";
        String test75= "a + b*c + d + e*f*g + h*i + j*(k + L + m*(n-p*q+r) - s*t)";
        String test76= "a-b*c+k";
        String test77= "(90+3) - 4*5 - 6*17/2 + 3*9*10 - 0.5*(11*12+13*14/7)";
        String test78= "a*b*c/d + e*f*g/h + t*(a-q) - 5.0*i - 4*j + k + L + m*n*k*(p-1) + sin(pi*R)*log(q)/sin(3*pi/4 + x*pi/2)";
        String test79= "a-b*(k-t+(f-g)*(f*5.9-q)+(w-y*(m-1))/p)-(x-3)*(x+3)/(d+q-w)";
        String test80= "cossfdadfaaf(a+b, c+d, f+g)";
        String test81= "a+b+c*d-cos(a, b + c)";
        String test82= "a+b+c*d-cos(a * f, b + c, g / d)+sin()";
        String test83= "a";
        String test84= "a-y*k+b*t-b*f*5.9+sin(pi*R)*log(q)/sin(3*pi/2 + x*pi/2)*q+g*f*5.9-g*q-f*(d+q)*s-g*(d+q)*p";
        long startTime = System.nanoTime();

        Configuration configuration = new Configuration(
                false,
                true,
                BoolArg.TRUE,
                List.of(),
                true,
                false,
                -1L,
                true,
                true,
                false
        );
        GlobalSettings.configure(configuration);

        ExpressionProcessor.process(test84);
        long endTime = System.nanoTime();

        Statistics.displayTime(startTime, endTime);

//        List<String> a = new ArrayList<>();
//        a.add(0, "a");
//        a.add(1, null);
//        a.add(2, "b");
//        System.out.println(a);
//        JFrame frame = new JFrame("JTable Example");
//        String[] columns = {"ID", "Name", "Age"};
//        Object[][] data = {
//                {1, "Alice", 25},
//                {2, "Bob", 30},
//                {3, "Charlie", 35}
//        };
//        JTable table = new JTable(data, columns);
//
//        JScrollPane scrollPane = new JScrollPane(table);
//        frame.add(scrollPane);
//        frame.setSize(400, 200);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);

//        for (int i = 0; i <= 100; i += 10) {
//            // Print percentage with \r to overwrite the line
//            System.out.print("\r" + i + "% completed");
//
//            // Simulate some work with sleep
//            Thread.sleep(2000);
//        }
//
//        System.out.println("\nDone!");  // Move to a new line when complete
//        DynamicList structureList = new DynamicList();
//        structureList.add(new TreeNode());
//        structureList.add(new DynamicList());
//        DynamicList structureList1 = new DynamicList();
//        structureList1.add(new TreeNode());
//        structureList.add(structureList1);
//        System.out.println(structureList);
//
//        System.out.println(new ArrayList<>());

//        List<Object> objectList = new ArrayList<>();
//        objectList.add(new TreeNode());
//        objectList.add(new DynamicList());
//        List<Object> objectList1 = new ArrayList<>();
//        objectList1.add(new TreeNode());
//        objectList.add(objectList1);
//        System.out.println(objectList);


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

//        List<Character> elements = List.of('a', 'b', 'c');
//        List<List<Character>> permutations = getPermutations(new ArrayList<>(elements));
//        System.out.println("All Permutations: " + permutations);
    }

//    public static List<List<Character>> getPermutations(List<Character> elements) {
//        List<List<Character>> result = new ArrayList<>();
//        permute(elements, 0, result);
//        return result;
//    }
//
//    private static void permute(List<Character> elements, int start, List<List<Character>> result) {
//        if (start == elements.size() - 1) {
//            result.add(new ArrayList<>(elements));
//            return;
//        }
//        for (int i = start; i < elements.size(); i++) {
//            swap(elements, start, i);
//            permute(elements, start + 1, result);
//            swap(elements, start, i);  // backtrack
//        }
//    }
//
//    private static void swap(List<Character> elements, int i, int j) {
//        Character temp = elements.get(i);
//        elements.set(i, elements.get(j));
//        elements.set(j, temp);
//    }
}