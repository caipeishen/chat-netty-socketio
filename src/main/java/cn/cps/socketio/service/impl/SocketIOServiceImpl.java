package cn.cps.socketio.service.impl;

import cn.cps.socketio.entity.PushMessage;
import cn.cps.socketio.service.SocketIOService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

/**
 * @Author: Cai Peishen
 * @Date: 2021/4/27 18:20
 * @Description:
 */
@Service(value = "socketIOService")
public class SocketIOServiceImpl implements SocketIOService {
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private SocketIOServer socketIOServer;
    
    /**
     * Spring IoC容器创建之后，在加载SocketIOServiceImpl Bean之后启动
     *
     * @throws Exception
     */
    @PostConstruct
    private void autoStartup() throws Exception {
        start();
    }
    
    /**
     * Spring IoC容器在销毁SocketIOServiceImpl Bean之前关闭,避免重启项目服务端口占用问题
     *
     * @throws Exception
     */
    @PreDestroy
    private void autoStop() throws Exception {
        stop();
    }
    
    @Override
    public void start() throws Exception {
        // 创建个人对个人聊天命名空间
        this.socketIOServer.addNamespace("chat-namespace");
        
        // 监听客户端连接
        this.socketIOServer.addConnectListener(client -> {
            PushMessage pushMessage = this.getParamsByClient(client);
            if (pushMessage.getSendUser() != null && pushMessage.getRoom() !=null) {
                client.joinRoom(pushMessage.getRoom());
                System.out.println(pushMessage.getSendUser()+"进入了"+pushMessage.getRoom()+"房间");
                System.out.println("SessionId:  " + client.getSessionId());
                System.out.println("RemoteAddress:  " + client.getRemoteAddress());
                System.out.println("Transport:  " + client.getTransport());
            }
        });
        
        // 监听客户端断开连接
        this.socketIOServer.addDisconnectListener(client -> {
            PushMessage pushMessage = this.getParamsByClient(client);
            if (pushMessage != null) {
                client.leaveRoom(pushMessage.getRoom());
                System.out.println(pushMessage.getSendUser()+"离开了"+pushMessage.getRoom()+"房间");
                client.disconnect();
            }
        });
        
        // 处理自定义的事件，与连接监听类似
        this.socketIOServer.addEventListener("text", Object.class, (client, data, ackSender) -> {
            // TODO do something
            client.getHandshakeData();
            PushMessage pushMessage = objectMapper.convertValue(data, PushMessage.class);
            System.out.println( " 客户端：************ " + pushMessage.toString());
            this.pushMessageToUser(pushMessage);
        });
    
        this.socketIOServer.start();
    }
    
    
    
    @Override
    public void stop() {
        if (this.socketIOServer != null) {
            this.socketIOServer.stop();
            this.socketIOServer = null;
        }
    }
    
    @Override
    public void pushMessageToUser(PushMessage pushMessage) throws JsonProcessingException {
        String sendUser = pushMessage.getSendUser();
        if (!StringUtil.isNullOrEmpty(sendUser)) {
            String dataJSON = objectMapper.writeValueAsString(pushMessage);
            this.socketIOServer.getRoomOperations(pushMessage.getRoom()).sendEvent(PUSH_EVENT, dataJSON);
        }
    }
    
    /**
     * 此方法为获取client连接中的参数，可根据需求更改
     * @param client
     * @return
     */
    private PushMessage getParamsByClient(SocketIOClient client) {
        PushMessage pushMessage = new PushMessage();
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> sendUserList = params.get("sendUser");
        List<String> roomList = params.get("room");
        List<String> contentList = params.get("content");
        if (sendUserList != null && sendUserList.size() > 0) {
            pushMessage.setSendUser(sendUserList.get(0));
        }
        if (roomList != null && roomList.size() > 0) {
            pushMessage.setRoom(roomList.get(0));
        }
        if (contentList != null && contentList.size() > 0) {
            pushMessage.setContent(contentList.get(0));
        }
        return pushMessage;
    }
    
}
