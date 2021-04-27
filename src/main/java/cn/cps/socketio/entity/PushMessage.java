package cn.cps.socketio.entity;

import lombok.*;

/**
 * @Author: Cai Peishen
 * @Date: 2021/4/27 18:21
 * @Description:
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushMessage {
    
    // 登录用户编号
    private String sendUser;
    
    // 接受用户
    private String receiveUser;
    
    // 推送内容
    private String content;
    
}
