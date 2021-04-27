package cn.cps.socketio.service;

import cn.cps.socketio.entity.PushMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @Author: Cai Peishen
 * @Date: 2021/4/27 18:20
 * @Description:
 */
public interface SocketIOService {
    
    //推送的事件
    public static final String PUSH_EVENT = "push_event";
    
    // 启动服务
    void start() throws Exception;
    
    // 停止服务
    void stop();
    
    // 推送信息
    void pushMessageToUser(PushMessage pushMessage) throws JsonProcessingException;
    
}