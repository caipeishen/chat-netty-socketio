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
    
    // 发送用户
    private String sendUser;
    
    // 聊天房间
    private String room;
    
    // 推送内容
    private String content;
    
}
