import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by duwei on 2018/3/17.
 */
public class WordCount {

    private static int charNum = 0;
    private static int wordNum = 0;
    private static int lineNum = 0;

    public static void main(String[] args) throws Exception {
        int n = args.length;

        String path = args[n-1];
        File file = new File(path);
        String fileName = file.getName();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(path));
        BufferedReader br = new BufferedReader(isr);

        while (br.read() != -1){
            String s = br.readLine();
            countChar(s);
            countWord(s);
            countLine(s);
        }

        String result1 = new String("字符数：" + charNum);
        String result2 = new String("单词数：" + wordNum);
        String result3 = new String("行数：" + lineNum);

        if (n==2){
            switch (args[0]){
                case "-c":
                    System.out.println(fileName + "," + result1);
                    break;
                case "-w":
                    //countWord
                    System.out.println(fileName + "," + result2);
                    break;
                case "-l":
                    //countLine
                    System.out.println(fileName + "," + result3);
                    break;
                case "-o":
                    //outputFile
                    String content = new String(fileName + "\r\n" + result1 + "\r\n" + result2 + "\r\n" + result3);
                    Path outPath = Paths.get("output.txt");
                    File output = new File(outPath.toString());
                    Files.write(outPath,content.getBytes());
                    break;
                default:
            }
        }
        else if (n==3){
            switch (args[0] + args[1]){
                case "-c-w":
                    System.out.println(fileName + "\n" + result1 + "\n" + result2);
                    break;
                case "-w-c":
                    System.out.println(fileName + "\n" + result2 + "\n" + result1);
                    break;
                case "-c-l":
                    System.out.println(fileName + "\n" + result1 + "\n" + result3);
                    break;
                case "-l-c":
                    System.out.println(fileName + "\n" + result3 + "\n" + result1);
                    break;
                case "-w-l":
                    System.out.println(fileName + "\n" + result2 + "\n" + result3);
                    break;
                case "-l-w":
                    System.out.println(fileName + "\n" + result3 + "\n" + result2);
                    break;
                default:
            }
        }
        else if (n==4){
            String content = new String(fileName + "\n" + result1 + "\n" + result2 + "\n" + result3);
            System.out.println(content);
        }
        else{
            System.out.println("命令错误！");
        }
    }
    public static int countChar(String s){
        charNum += s.length();
        int blankNum = s.split("").length - 1;
        charNum += blankNum;
        return charNum;
    }
    public static int countWord(String s){
        String a = ",";
        s.replaceAll(a," ");
        wordNum += s.split(" ").length;
        return wordNum;
    }
    public static int countLine(String s){
        lineNum++;
        return lineNum;
    }
}
