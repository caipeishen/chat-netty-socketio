package cn.cps.socketio.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Cai Peishen
 * @Date: 2021/4/28 9:35
 * @Description:
 */
@Controller
public class SocketController {
    
    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
}
