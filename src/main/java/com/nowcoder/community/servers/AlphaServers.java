package com.nowcoder.community.servers;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AlphaServers {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaServers(){
        System.out.println("实例化AlphaServers");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaServers");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaServers");
    }

    public String find(){
        return alphaDao.select();
    }
}
