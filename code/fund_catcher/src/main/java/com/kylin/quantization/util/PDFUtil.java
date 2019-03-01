package com.kylin.quantization.util;
import java.io.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
public class PDFUtil {
    public static String  getText(String file)  {
        try {
            return parse(PDDocument.load(new File(file)));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String  getText(InputStream in) {
        try {
            return parse(PDDocument.load(in));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String parse(PDDocument document){
        // 是否排序
        boolean sort = false;
        // 编码方式
        String encoding = "UTF-8";
        // 开始提取页数
        int startPage = 1;
        // 结束提取页数
        int endPage = Integer.MAX_VALUE;
        try{

            // 采用PDFTextStripper提取文本
            PDFTextStripper stripper = new PDFTextStripper();
            // 设置是否排序
            stripper.setSortByPosition(sort);
            // 设置起始页
            stripper.setStartPage(startPage);
            // 设置结束页
            stripper.setEndPage(endPage);
            String text = stripper.getText(document);

            //尝试把前边或后边接有空白字符的换行符换成其他的文字，然后把换行符替换掉，之后再把其他文字换成换行符
            //原理是pdf转成String中间有过多的回车换行符\r\n这种，但是如果换行符前后都是有文字的（不为空），则这应该是一个被pdf强行换行出来的
            text = text.replaceAll("\\r\\n\\s","\001");  //这里的Jacck最好换成一个更复杂的文本，作为中间替换物存在尽量在中间转化过程中和文档中没有任何匹配
            text = text.replaceAll("\\s\\r\\n","\001");
            text = text.replaceAll("\\n|\\r","");       //处理掉被强行加上来的回车换行符
            text = text.replaceAll("\001","\r\n");
            return text;
//            stripper.writeText(document, output);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(document != null){
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
