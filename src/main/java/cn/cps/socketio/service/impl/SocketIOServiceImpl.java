package cn.cps.socketio.service.impl;

import cn.cps.socketio.entity.PushMessage;
import cn.cps.socketio.service.SocketIOService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.handler.SocketIOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Cai Peishen
 * @Date: 2021/4/27 18:20
 * @Description:
 */
@Service(value = "socketIOService")
public class SocketIOServiceImpl implements SocketIOService {
    
    // 用来存已连接的客户端
    private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
    
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
        // 监听客户端连接
        this.socketIOServer.addConnectListener(client -> {
            String sendUser = getParamsByClient(client);
            if (sendUser != null) {
                System.out.println(sendUser);
                System.out.println("SessionId:  " + client.getSessionId());
                System.out.println("RemoteAddress:  " + client.getRemoteAddress());
                System.out.println("Transport:  " + client.getTransport());
                if (clientMap.get(sendUser) == null) {
                    this.clientMap.put(sendUser, client);
                } else {
                    throw new SocketIOException("该昵称在线，请切换昵称~~");
                }
            }
        });
        
        // 监听客户端断开连接
        this.socketIOServer.addDisconnectListener(client -> {
            String sendUser = getParamsByClient(client);
            if (sendUser != null) {
                this.clientMap.remove(sendUser);
                System.out.println("断开连接： " + sendUser);
                System.out.println("断开连接： " + client.getSessionId());
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
            // 有接受者则发送接收者，无则发给所有人
            if (StringUtil.isNullOrEmpty(pushMessage.getReceiveUser())) {
                // 和所有人沟通
                this.clientMap.values().forEach(client -> {
                    if (client != null) {
                        client.sendEvent(PUSH_EVENT, dataJSON);
                    }
                });
            } else {
                // 具体个人
                SocketIOClient client = this.clientMap.get(pushMessage.getReceiveUser());
                if (client != null) {
                    client.sendEvent(PUSH_EVENT, dataJSON);
                }
            }
        }
    }
    
    /**
     * 此方法为获取client连接中的参数，可根据需求更改
     * @param client
     * @return
     */
    private String getParamsByClient(SocketIOClient client) {
        // 从请求的连接中拿出参数（这里的sendUser必须是唯一标识）
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> list = params.get("sendUser");
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    public static Map<String, SocketIOClient> getClientMap() {
        return clientMap;
    }
    
    public static void setClientMap(Map<String, SocketIOClient> clientMap) {
        SocketIOServiceImpl.clientMap = clientMap;
    }
}
