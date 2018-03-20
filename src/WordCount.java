import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by duwei on 2018/3/20.
 */
public class WordCount {
    private static int charNum;
    private static int wordNum;
    private static int lineNum;
    private static int blankLineNum;
    private static int annotationLineNum;
    private static int codeLineNum;

    public static void main(String[] args)throws Exception {
        baseFunction(args);
        extendFunction(args);
    }
    public static void baseFunction(String[] strings) throws IOException{
        File file = getFile(strings);
        List<String> list = Arrays.asList(strings);

        baseCount(file);

        if (list.contains("-c")){
            //countChar()
            String result = new String(file.getName() + "," +"字符数：" + charNum);
            System.out.println(result);
            print(result);
        }
        if (list.contains("-w")&& !list.contains("-e")){
            //countWord()
            String result = new String(file.getName() + "," + "单词数：" + wordNum);
            System.out.println(result);
            print(result);
        }
        if (list.contains("-l")){
            //counLine()
            String result = new String(file.getName() + "," + "行数：" + lineNum);
            System.out.println(result);
            print(result);
        }
        if (list.contains("-o")){
            String outFileName = list.get(list.indexOf("-o")+1) ;
            Path outPath = Paths.get(outFileName);
            String content = new String(file.getName() + "\r\n"
                    + "字符数：" + charNum
                    + "\r\n" + "单词数：" + wordNum
                    + "\r\n" +"行数：" + lineNum);
            Files.write(outPath,content.getBytes());
        }

    }
    public static void extendFunction(String[] strings) throws IOException{
        File file = getFile(strings);
        List<String> list = Arrays.asList(strings);

        if (!file.isDirectory()){
            extendCount(file);
        }else if (file.isDirectory())
        {
            recFile(file);
        }else {
            System.out.println("ERROR!");
        }

        if (list.contains("-s")){
            recFile(file);
        }
        if (list.contains("-a")){
            String string = new String(file.getName() +",代码行 / 空行 / 注释行：" + codeLineNum + "/" + blankLineNum + "/" + annotationLineNum);
            System.out.println(string);
            print(string);
        }
        if (list.contains("-w")&&list.contains("-e")){
            baseCount(file);
            String s1 = list.get(list.indexOf("-w")+1);
            String s2 = list.get(list.indexOf("-e")+1);
            File file1 = new File(s1);
            File file2 = new File(s2);
            int sameNum = stopList(file1,file2);
            String string = new String(file.getName() +",单词数：" + (wordNum - sameNum));
            System.out.println(string);
            print(string);
        }
    }
    public static File getFile(String[] strings)throws IOException{
        String fileString = null;
        for (String s : strings){
            if (s.endsWith(".c")){
                fileString = s;
            }
        }
        File file = new File(fileString);
        return file;
    }
    public static File print(String content)throws IOException{
        Path outPath = Paths.get("result.txt");
        File result = new File(outPath.toString());
        Files.write(outPath,content.getBytes());
        return result;
    }
    public static BufferedReader getBr(File file)throws IOException{
        String fileName = file.getName();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName));
        BufferedReader br = new BufferedReader(isr);
        return br;
    }
    public static void baseCount(File file) throws IOException{
        BufferedReader br = getBr(file);
        String s = null;
        while ((s = br.readLine()) != null){
            s = br.readLine();
            countChar(s);
            countWord(s);
            countLine(s);
        }
    }
    public static int countChar(String s){
        charNum += s.length();
        int blankNum = s.split(" ").length - 1;
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
    public static void extendCount(File file)throws IOException{
        if (file == null || !file.exists())
            throw new FileNotFoundException(file + "，文件不存在！");

        BufferedReader bufr = null;
        try {
            // 将指定路径的文件与字符流绑定
            bufr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(file + "，文件不存在！" + e);
        }

        // 定义匹配每一行的正则匹配器
        Pattern annotationLinePattern = Pattern.compile("((//)|(/\\*+)|((^\\s)*\\*)|((^\\s)*\\*+/))+",
                Pattern.MULTILINE + Pattern.DOTALL);    // 注释匹配器(匹配单行、多行、文档注释)

        Pattern blankLinePattern = Pattern.compile("^\\s*$");    // 空白行匹配器（匹配回车、tab键、空格）

        Pattern codeLinePattern = Pattern.compile("(?!import|package).+;\\s*(((//)|(/\\*+)).*)*",
                Pattern.MULTILINE + Pattern.DOTALL); // 代码行匹配器（以分号结束为一行有效语句,但不包括import和package语句）

        // 遍历文件中的每一行，并根据正则匹配的结果记录每一行匹配的结果
        String line = null;
        try {
            while ((line = bufr.readLine()) != null) {
                if (annotationLinePattern.matcher(line).find()) {
                    annotationLineNum++;
                }

                if (blankLinePattern.matcher(line).find()) {
                    blankLineNum++;
                }

                if (codeLinePattern.matcher(line).matches()) {
                    codeLineNum++;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("读取文件失败！" + e);
        } finally {
            try {
                bufr.close();    // 关闭文件输入流并释放系统资源
            } catch (IOException e) {
                throw new RuntimeException("关闭文件输入流失败！");
            }
        }
    }
    public static int stopList(File file1,File file2) throws IOException{
        String[] strings1 = getWord(file1);
        String[] strings2 = getWord(file2);
        return sameNum(strings1,strings2);
    }
    public static String[] getWord(File file) throws IOException{
        BufferedReader br = getBr(file);
        boolean eof = false;
        String s;
        String[] content = null;
        while (!eof){
            s = br.readLine();
            if(s == null) {
                eof = true;
            } else{
                String a = ",";
                if (s.contains(a)) {
                    s.replaceAll(a, " ");
                }
                content = s.split(" ");
            }

        }
        return content;
    }
    public static int sameNum(String[] a,String[] b){
        ArrayList<String> same = new ArrayList<String>();
        ArrayList<String> temp = new ArrayList<String>();

        for (int i = 0; i < a.length; i++) {
            temp.add(a[i]);   //把数组a中的元素放到Set中，可以去除重复的元素
        }

        for (int j = 0; j < b.length; j++) {
            //把数组b中的元素添加到temp中
            //如果temp中已存在相同的元素，则temp.add（b[j]）返回false
            if(!temp.add(b[j]))
                same.add(b[j]);
        }
        return same.size();
    }
    public static void recFile(File file) throws IOException{
        File[] files = file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".c") || pathname.isDirectory();
            }
        });
        String string =  null;
        for (File target : files) {
            extendCount(target);
            baseCount(target);
            string += new String(target.getName()+",字符数：" + charNum +"\r\n"
                    + target.getName()+",单词数：" + wordNum +"\r\n"
                    + target.getName() +",代码行 / 空行 / 注释行：" + codeLineNum + "/" + blankLineNum + "/" + annotationLineNum+"\r\n");
        }
        print(string);
    }

}
