package com.mise.postcenter.service.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;

public class TFIDFAnalyzer
{

    static HashMap<String,Double> idfMap;
    static HashSet<String> stopWordsSet;
    static double idfMedian;

    public List<Keyword> analyze(String content,int topN){
        List<Keyword> keywordList=new ArrayList<>();

        if(stopWordsSet==null) {
            stopWordsSet=new HashSet<>();
            loadStopWords(stopWordsSet, this.getClass().getResourceAsStream("/stop_words.txt"));
        }
        if(idfMap==null) {
            idfMap=new HashMap<>();
            loadIDFMap(idfMap, this.getClass().getResourceAsStream("/idf_dict.txt"));
        }

        Map<String, Double> tfMap=getTF(content);
        for(String word:tfMap.keySet()) {
            // 若该词不在idf文档中，则使用平均的idf值(可能定期需要对新出现的网络词语进行纳入)
            if(idfMap.containsKey(word)) {
                keywordList.add(new Keyword(word,idfMap.get(word)*tfMap.get(word)));
            }else
                keywordList.add(new Keyword(word,idfMedian*tfMap.get(word)));
        }

        Collections.sort(keywordList);

        if(keywordList.size()>topN) {
            int num=keywordList.size()-topN;
            for(int i=0;i<num;i++) {
                keywordList.remove(topN);
            }
        }
        return keywordList;
    }

    private Map<String, Double> getTF(String content) {
        Map<String,Double> tfMap=new HashMap<>();
        if(content==null || content.equals(""))
            return tfMap;

        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> segments=segmenter.sentenceProcess(content);
        Map<String,Integer> freqMap=new HashMap<>();

        int wordSum=0;
        for(String segment:segments) {
            //停用词不予考虑，单字词不予考虑
            if(!stopWordsSet.contains(segment) && segment.length()>1) {
                wordSum++;
                if(freqMap.containsKey(segment)) {
                    freqMap.put(segment,freqMap.get(segment)+1);
                }else {
                    freqMap.put(segment, 1);
                }
            }
        }

        // 计算double型的tf值
        for(String word:freqMap.keySet()) {
            tfMap.put(word,freqMap.get(word)*0.1/wordSum);
        }

        return tfMap;
    }

    private void loadStopWords(Set<String> set, InputStream in){
        BufferedReader bufr;
        try
        {
            bufr = new BufferedReader(new InputStreamReader(in));
            String line=null;
            while((line=bufr.readLine())!=null) {
                set.add(line.trim());
            }
            try
            {
                bufr.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadIDFMap(Map<String,Double> map, InputStream in ){
        BufferedReader bufr;
        try
        {
            bufr = new BufferedReader(new InputStreamReader(in));
            String line=null;
            while((line=bufr.readLine())!=null) {
                String[] kv=line.trim().split(" ");
                map.put(kv[0],Double.parseDouble(kv[1]));
            }
            try
            {
                bufr.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // 计算idf值的中位数
            List<Double> idfList=new ArrayList<>(map.values());
            Collections.sort(idfList);
            idfMedian=idfList.get(idfList.size()/2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args)
    {
        String content="孩子上了幼儿园 安全防拐教育要做好";
        int topN=5;
        TFIDFAnalyzer tfidfAnalyzer=new TFIDFAnalyzer();
        List<Keyword> list=tfidfAnalyzer.analyze(content,topN);
        for(Keyword word:list)
            System.out.print(word.getName()+":"+word.getTfidfvalue()+",");
    }*/
}