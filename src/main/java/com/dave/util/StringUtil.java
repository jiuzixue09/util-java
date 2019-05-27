package com.dave.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtil {

    /**
     * 匹配单个字符全拼的声母和韵母,声母可能不存在.注意y和w,虽然不在声母范围,但是居首也是有可能的
     */
    private static final Pattern psp = Pattern
            .compile("^([bpmfdtnlgkhjqxrzcsyw]{0,2})([aeiouv][a-z]*)([0-9]*)$");

    private static HanyuPinyinOutputFormat hpof;

    private static Properties wb86;

    private static Properties shuangPin;

    private static Properties yueYuPin;

    private static Properties wordStructure;

    private static Properties wordStrokeCount;

    private static Properties readProperties(String configName) {
        Properties prop = new Properties();
        InputStream strm = null;
        try {
            File fname = new File(configName);
            if (fname.canRead()) {
                System.out.println("Using configuration file: " + fname.toString());
                strm = new FileInputStream(fname);
            } else {
                System.out.println("Configuration file " + fname.toString() + " not found, loading it from resources");
                strm = StringUtil.class.getResourceAsStream("/" + configName);
                if (strm == null) throw new RuntimeException("Cannot find resource: " + configName);
            }
            prop.load(new InputStreamReader(strm, "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } // config.property load failures are serious matters...
        finally {
            if (strm != null) try {
                strm.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return prop;

    }

    static {
        // 拼音格式
        hpof = new HanyuPinyinOutputFormat();
        hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出全小写
        //hpof.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不显示音调
        hpof.setVCharType(HanyuPinyinVCharType.WITH_V);// ü替换为v

        // 五笔数据
        wb86 = readProperties("wb86.properties");
        // 双拼
        shuangPin = readProperties("sp.properties");

        yueYuPin = readProperties("yueyu.properties");

        wordStructure = readProperties("word_structure_dict_20000.properties");

        wordStrokeCount = readProperties("word_stroke_count_dict_20000.properties");

    }

    /**
     * 字符转化为拼音.无法转换返回null,转换成功返回所有的发音
     *
     * @param c
     * @return
     */
    public static String[] charToPinyin(char c) {
        if (c < 0x4E00 || c > 0x9FA5) {// GBK字库在unicode中的起始和结束位置
            if (c != 0x3007) {// 圆圈0比较特殊,需要处理一下
                return null;
            }
        }
        try {
            return PinyinHelper.toHanyuPinyinStringArray(c, hpof);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            return null;
        }
    }

    /**
     * 获取单个字符的双拼,顺序和全拼一致
     *
     * @param c
     * @return
     */
    public static String[] charToShuangPin(char c) {
        String[] array = charToPinyin(c);
        if (array == null) {
            return array;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            Matcher m = psp.matcher(s);
            if (m.matches()) {
                String sm = m.group(1);
                String smdz = shuangPin.getProperty(sm);
                String ym = m.group(2);
                String ymdz = shuangPin.getProperty(ym);

                String r = "";
                if (smdz != null) {
                    r = smdz;
                }
                if (ymdz != null) {
                    r += ymdz;
                }

                result[i] = r;
            } else {
                System.err.println("分解" + c + "拼音的拼音时发生错误!无法拆分出声母和韵母.");
            }
        }
        return result;
    }

    public static String[] pinyinToSyllable(String s){
        Matcher m = psp.matcher(s);
        String[] rs = new String[3];
        if (m.matches()) {
            String sm = m.group(1);
            String smdz = shuangPin.getProperty(sm);
            String ym = m.group(2);
            String ymdz = shuangPin.getProperty(ym);

            String yd =  m.group(3);

            rs[0] = smdz;
            rs[1] = ymdz;
            rs[2] = yd;
            return rs;
        }
        return null;
    }

    /**
     * 返回字符串的拼音的首字母,每个多音字只取第一个发音.
     *
     * @param s
     * @return
     */
    public static String toPinyinShouZiMu(String s) {

        if (s == null || s.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            String[] r = charToPinyin(c);
            if (r == null) {
                sb.append(c);
            } else {
                String py = r[0];
                if (py.length() > 0) {
                    sb.append(py.charAt(0));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 返回字符串的拼音,全拼,每个字的首字母大写,每个多音字只取第一个发音.
     *
     * @param s
     * @return
     */
    public static String toQuanPin(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            String[] r = charToPinyin(c);
            if (r == null) {
                sb.append(c);
            } else {
                String py = r[0];
                if (py.length() > 0) {
                    char f = Character.toUpperCase(py.charAt(0));
                    // if ((f == 'A' || f == 'O' || f == 'E') && sb.length() >
                    // 0) {
                    // aoe 开头的音节连接在其它音节后面的时候，如果音节的界限发生混淆，用隔音符号（'）隔开，例如
                    // pí'ǎo（皮袄）xī'ān（西安）。
                    // py = "'" + f + py.substring(1);
                    // } else {
                    py = f + py.substring(1);
                    // }
                }
                sb.append(py);
            }
        }
        return sb.toString();
    }

    /**
     * 返回字符串的双拼.多音字只取第一个发音.每个字的首字母大写
     *
     * @param s
     * @return
     */
    public static String toShuangPin(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            String[] r = charToShuangPin(c);
            if (r == null) {
                sb.append(c);
            } else {
                String py = r[0];
                if (py.length() > 0) {
                    char f = Character.toUpperCase(py.charAt(0));
                    py = f + py.substring(1);
                }
                sb.append(py);
            }
        }
        return sb.toString();
    }

    public static int charToStruct(char c){
        String wordStructure = StringUtil.wordStructure.getProperty(String.valueOf(c));
        if(StringUtils.isBlank(wordStructure)) return -1;
        return Integer.parseInt(wordStructure);
    }

    public static int charToStrokeCount(char c){
        String strokeCount = wordStrokeCount.getProperty(String.valueOf(c));
        if(StringUtils.isBlank(strokeCount)) return -1;
        return Integer.parseInt(strokeCount);
    }

    public static String[] charToYueyuPin(char c){
        if (c < 0x4E00 || c > 0x9FA5) {// GBK字库在unicode中的起始和结束位置
            return null;
        }

        String result = yueYuPin.getProperty(Integer.toHexString(c).toUpperCase());
        if (result == null) {
            return null;
        }
        if (result.contains(",")) {
            return result.split(",");
        } else {
            return new String[] { result };
        }

    }

    public static String[] stringToYueyuPin(String s){
        LinkedList<Character> list = new LinkedList<>();
        for (char c : s.toCharArray()) {
            list.add(c);
        }

        return combination(list);
    }

    private static String[] combination(LinkedList<Character> list){
        Character c = list.pollFirst();
        String[] rs = charToYueyuPin(c);
        Set<String> collect = Stream.of(rs).map(it -> it.substring(0, it.length() - 1)).collect(Collectors.toSet());
        rs = collect.toArray(new String[collect.size()]);


        if(list.isEmpty()) {
            return rs;
        }else{
            String[] combination = combination(list);

            List<String> tmp = new ArrayList<>();
            for (String r : rs) {
                for (String s : combination) {
                    tmp.add(r + " " + s);
                }
            }
            return tmp.toArray(new String[tmp.size()]);
        }

    }



    /**
     * 字符转化为五笔(86),无法转化返回null
     *
     * @param c
     * @return
     */
    public static String[] charToWubi(char c) {
        if (c < 0x4E00 || c > 0x9FA5) {// GBK字库在unicode中的起始和结束位置
            return null;
        }
        String result = wb86.getProperty(Integer.toHexString(c).toUpperCase());
        if (result == null) {
            return null;
        }
        if (result.contains(",")) {
            return result.split(",");
        } else {
            return new String[] { result };
        }
    }
//


    /**
     * 获取汉字串拼音，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String cn2Spell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }

    public static String getPinYin(String strs) {

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // WITH_TONE_NUMBER/WITHOUT_TONE/WITH_TONE_MARK
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        char[] ch = strs.trim().toCharArray();
        StringBuffer buffer = new StringBuffer("");

        try {
            for (int i = 0; i < ch.length; i++) {
                // unicode，bytes应该也可以.
                if (Character.toString(ch[i]).matches("[\u4e00-\u9fa5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ch[i], format);
                    buffer.append(temp[0]);
                    //buffer.append(" ");
                } else {
                    buffer.append(ch[i]);
                }

                buffer.append(" ");
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


    //
    public static boolean isSimpleChinese(String str) {
        try {
            if (str.equals(new String(str.getBytes("gb2312"), "gb2312"))) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isComplexChinese(String str) {
        try {
            if (str.equals(new String(str.getBytes("big5"), "big5"))) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String[] rs = stringToYueyuPin("西游记仙履奇缘");
        System.out.println(Arrays.deepToString(rs));
        rs = stringToYueyuPin("戏王之王");
        System.out.println(Arrays.deepToString(rs));
        rs = stringToYueyuPin("铁三角");
        System.out.println(Arrays.deepToString(rs));
        rs = stringToYueyuPin("开心魔法");
        System.out.println(Arrays.deepToString(rs));
        System.out.println(pinyinToSyllable(charToPinyin('儿')[0]));
        System.out.println(charToStrokeCount('我'));
        System.out.println(charToStruct('我'));
        System.out.println(toQuanPin("单独的确执拗拗口占卜"));
        System.out.println(Arrays.deepToString(pinyinToSyllable("chuang4")));
        System.out.println(toShuangPin("中华人民共和国--中联部"));
        System.out.println(toPinyinShouZiMu("中华人民共和国--中联部"));
        System.out.println(toShuangPin("壹仟贰佰叁拾肆亿伍仟陆佰柒拾捌万玖仟零壹拾贰元叁角肆分"));
        System.out.println(Arrays.deepToString(charToWubi('娘')));

        System.out.println(Arrays.deepToString(charToWubi('狼')));
        System.out.println(Arrays.deepToString(charToWubi('浪')));
        System.out.println(Arrays.deepToString(charToWubi('如')));
        System.out.println(Arrays.deepToString(charToWubi('扣')));
        System.out.println(Arrays.deepToString(charToWubi('A')));
        System.out.println(Arrays.deepToString(charToWubi('工')));
        System.out.println(Arrays.deepToString(charToWubi('好')));
        System.out.println(Arrays.deepToString(charToWubi('炎')));
        System.out.println(Arrays.deepToString(charToWubi('焱')));
        System.out.println(Arrays.deepToString(charToWubi('火')));

        String str = "要解决我们的问题以及其它类似问题需要一个高效的词典查询算法树就是其中一种解决的问题大致如下有一个大规模的词典有一大段文本通过最少次扫描得出这段文本在词典中的所有词衍生的问题是有一段文本有一个词典从文本开头算起在词典中最长的词是什么树算法解决了这个问题一般的树虽然效率很高但是构造字典需要的内存太大所以常用的是我们的算法用的就是这个最长匹配算法在分词领域基本问题是给一段文本如何切分成单词的序列这个序列与人的理解大致相同英文问题不大有明显的分隔符但是中文或者中文的拼音就不灵了最长匹配算法是其中一种就是用一个字典通常前面说的树字典开头切分出最长的词然后拿掉他在剩下的继续最长切分万一某位置没有词就算一个单字词拿掉这个单字词继续切分最长匹配算法的优点是快几乎没有更快的方案了缺点是不能解决很多歧义问题此外没有词就切出一个单字词也不是很好反向最长匹配算法与最长匹配类似只不过算法从文本的尾部开始往前匹配虽然没有理论依据实践表明效果略好于最长匹配最短路径匹配算法切分出每一种结果选取词数目最少的一种如果有两条以上路径词数最少没有说明选哪个算法统计每一个词与相邻词共同出现的词频依此做一个概率模型然后每一个组合都依据概率设置一个权值最后挑选权值最大的结果由于只考虑每一个词的相邻词有一个著名的算法可以优化评估分支避免每一个分支路径都计算一下总的来说被称为算法这个效果好于前面的算法问题是需要语料统计词频我们的方案基本思路我们将拼音视为分词问题准备一份拼音字典将一串拼音切分出一个最有可能的序列就是结果如果我们的字典很充分那么无法得到一个切分结果的就是中间有字母造成断层就认为不是拼音拼音字典来自项目字典格式未改我们把字典文件名改了一下由于我们只处理拼音不需要字典中的汉字算法实现词典就是拼音词典算法类似于但是权值计算简化为从从尾部往前看最长的组合权最大需要的数据结构";

        StringBuffer sb = new StringBuffer();
        for (char c : str.toCharArray()) {
            try {
                String pinyin = charToPinyin(c)[0];
                pinyin = pinyin.substring(0, pinyin.length() -1);
                sb.append(pinyin + " ");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}