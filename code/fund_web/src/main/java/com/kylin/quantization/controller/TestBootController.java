package com.kylin.quantization.controller;

import com.kylin.quantization.model.User;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: TestBootController
 * Description:
 * Author: aierxuan
 * Date: 2019-01-24 15:54
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
@RequestMapping("/testboot")
@Controller
public class TestBootController {
    @RequestMapping("getuser")
    public String getUser() {
        System.out.println("getUser is running");
        return "index";
    }
}
