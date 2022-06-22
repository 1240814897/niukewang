package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TireNode root = new TireNode();

    @PostConstruct
    public void init(){
        try(
                //把敏感词读入到字节流中
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //把字节流中数据进行存入缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    //添加字符到子节点
    private void addKeyword(String keyword) {
        //临时节点指向根节点
        TireNode tempNode = root;
        //遍历敏感词汇
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            //给子串赋值
            TireNode subNode = tempNode.getSubNode(c);

            if(subNode == null){
                //初始化子节点
                subNode = new TireNode();
                //添加一个节点
                tempNode.addSubNode(c,subNode);
            }
            //把刚刚创建的子节点连接临时节点，也就是父节点
            tempNode = subNode;

            //设置结束标识
            if(i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * 参数 带过滤文本
     * return 过滤后文本
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)) return null;
        //指针1，指向root
        TireNode tempNode = root;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //可变数组
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()) {
            //越界仍未找到敏感词时
            if(position < text.length()){
            //每轮23指针指向的数值一样
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)) {
                //判断是1指针否在根节点,是则计入结果，让指针2向下走
                if (tempNode == root) {
                    sb.append(c);
                    begin++;
                }
                //指针3需一直向下走
                position++;
                continue;
            }

            //获取子节点的值
            tempNode = tempNode.getSubNode(c);
            //判断c元素是不是意思敏感词
            if (tempNode == null) {
                //不是敏感词添入到数组中
                sb.append(text.charAt(begin));
                //把指针2向下找，指针3跟着
                position = ++begin;
                //回到根节点
                tempNode = root;
            } else if (tempNode.isKeywordEnd()) {//找到标记的敏感词
                //替换敏感词
                sb.append(REPLACEMENT);
                //3指针向前移动，2指针跟随
                begin = ++position;
                //回到根节点
                tempNode = root;
            }else{
                //可能疑似敏感词
                position++;

            }

        }else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = root;
            }
        }
        return sb.toString();
    }

    //判断是否有符号
    private boolean isSymbol(char c) {
        //0x2E80~0x9FFF是东南亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TireNode{
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点（key式下级字符，value是下级节点）
        private Map<Character,TireNode> subNode = new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TireNode node){
             subNode.put(c,node);
        }

        //获取子节点
        public TireNode getSubNode(Character c){
            return subNode.get(c);
        }
    }
}
