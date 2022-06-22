package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import org.json.JSONException;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Communityutil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密
    //hello->abc123def456
    //hello+3e4a8->abc123def456abc
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());

    }

    //封装JSON
    public static String getJSONString(int code,String msg,Map<String,Object> map){
        //fastjson也是JSON格式
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map != null){
            //遍历map装入json中
            for(String key:map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }
    //重载
    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }
    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",15);
        System.out.println(getJSONString(0,"ok",map));
    }

}
