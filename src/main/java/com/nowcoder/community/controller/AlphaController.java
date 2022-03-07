package com.nowcoder.community.controller;

import com.nowcoder.community.servers.AlphaServers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaServers alphaServers;
    @ResponseBody
    @RequestMapping("/seedate")
    public String seedate(){
        return alphaServers.find();
    }


    @ResponseBody
    @RequestMapping("/hello")
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getContextPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType(("text/html;charset=utf-8"));
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }


    }

    @RequestMapping(value = "/student",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some student";

    }

    @RequestMapping(value = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    @RequestMapping(value = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name , int age){

        System.out.println(name+":"+age);
        return "success";
    }

    @RequestMapping(value = "/teacher",method = RequestMethod.GET)
    public ModelAndView teacher(){
        ModelAndView mov = new ModelAndView();
        mov.addObject("name","张三");
        mov.addObject("age",30);
        mov.setViewName("/demo/view");

        return mov;
    }
    @RequestMapping(value = "/school",method = RequestMethod.GET)
    public String school(Model model){
        model.addAttribute("name","梧州学院");
        model.addAttribute("age","50");
        return "/demo/view";
    }

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",8000.00);
        return map;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",8000.00);
        list.add(map);

        map = new HashMap<>();
        map.put("name","李四");
        map.put("age",24);
        map.put("salary",9000.00);
        list.add(map);

        map = new HashMap<>();
        map.put("name","王五");
        map.put("age",26);
        map.put("salary",10000.00);
        list.add(map);
        return list;
    }
}
