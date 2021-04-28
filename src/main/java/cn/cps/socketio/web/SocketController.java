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
    
    // 所有人对话
    @RequestMapping({"/", "sb-all"})
    public String sbToAll() {
        return "sb-all";
    }
    
}
