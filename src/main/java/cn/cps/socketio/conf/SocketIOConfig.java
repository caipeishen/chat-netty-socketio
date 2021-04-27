package cn.cps.socketio.conf;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Cai Peishen
 * @Date: 2021/4/27 16:40
 * @Description: socket.io全局的配置对象
 */
@Configuration
public class SocketIOConfig {
    
    @Value("${socketio.host}")
    private String host;
    
    @Value("${socketio.port}")
    private Integer port;
    
    @Value("${socketio.bossCount}")
    private int bossCount;
    
    @Value("${socketio.workCount}")
    private int workCount;
    
    @Value("${socketio.allowCustomRequests}")
    private boolean allowCustomRequests;
    
    @Value("${socketio.upgradeTimeout}")
    private int upgradeTimeout;
    
    @Value("${socketio.pingTimeout}")
    private int pingTimeout;
    
    @Value("${socketio.pingInterval}")
    private int pingInterval;
    
    /**
     * 以下配置在上面的application.properties中已经注明
     * @return
     */
    @Bean
    public SocketIOServer socketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        return new SocketIOServer(config);
    }
    
}