package com.nowcoder.community.controller;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.servers.DiscussPostService;
import com.nowcoder.community.servers.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//注释代表这是控制层
@Controller
public class HomeController {

    //自动注入bean
    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    //路径映射，get请求
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model,Page page){
        //写入条目数和路径
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPost(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post: list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserByid(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";

    }

}
