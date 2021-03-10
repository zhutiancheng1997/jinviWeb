package com.ztcwork.demo.controller;

import com.ztcwork.demo.entity.dcColumn;
import com.ztcwork.demo.entity.dcPDO;
import com.ztcwork.demo.service.DTBaseService;
import com.ztcwork.demo.utils.GzipUtil;
import com.ztcwork.demo.vo.ColumnVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口文档
 * http://www.docway.net/project/1d296LGDF3J/1d29a5QNjQu
 */
@Controller
public class IndexContoller {

//    @RequestMapping(value ="/",method = RequestMethod.GET)
//    public String home(){
//        return "home.html";
//    }
    @RequestMapping(value ="/",method = RequestMethod.GET)
    public String index(){
        return "work.html";
    }

    @RequestMapping(value ="/work",method = RequestMethod.GET)
    public String work(){
        return "work.html";
    }

    @RequestMapping(value ="/work1",method = RequestMethod.GET)
    public String work1(){
        return "work1.html";
    }

    @RequestMapping(value ="/work2",method = RequestMethod.GET)
    public String work2(){
        return "work2.html";
    }

    @RequestMapping(value ="/demo",method = RequestMethod.GET)
    public String test(){
        return "index123.html";
    }



}
