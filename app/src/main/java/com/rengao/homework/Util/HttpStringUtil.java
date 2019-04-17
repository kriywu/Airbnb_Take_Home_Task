package com.rengao.homework.Util;

import com.rengao.homework.Module.Project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. 根据name和page生成URL
 * 2. 解析JSON数据
 */
public class HttpStringUtil {
    public static String generateURL(String name,int page){
        return Constants.PREFIX + name + Constants.CONDITION + page;
    }
    /**
     * @param body 请求返回的数据
     * @return 解析成功的project数组
     * @throws JSONException 解析出现问题：not found，拒绝访问....
     */
    public static List<Project> parse(String body) throws JSONException {
        List<Project> temp = new ArrayList<>();
        JSONArray array = new JSONArray(body);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Project project = new Project(object.getString("name"));
            project.star = object.getInt("stargazers_count");
            JSONObject o = object.getJSONObject("owner");
            project.avatar = o.getString("avatar_url");
            temp.add(project);
        }
        return temp;
    }
}
