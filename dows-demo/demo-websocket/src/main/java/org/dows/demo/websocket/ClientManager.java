package org.dows.demo.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.dows.demo.websocket.proto.MessageProto;
import org.dows.demo.websocket.util.BlankUtil;
import org.dows.framework.api.AccountInfo;
import org.dows.framework.websocket.NettyUtil;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class ClientManager {
    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    // 在线用户总数
    private static AtomicInteger accountCount = new AtomicInteger(0);

    private static ConcurrentMap<String, ConcurrentMap<Channel, AccountInfo>> onlineAccount = new ConcurrentHashMap<>();

    // 设备集合  key 房间号 value：设备集合
    private static ConcurrentMap<String, List<String>> equipments = new ConcurrentHashMap<>();

    /**
     * 保存用户身份信息
     *
     * @param channel 通道
     * @param nick    用户
     * @param room    房间数据
     * @return
     */
    public static AccountInfo saveUser(Channel channel, String nick, String room) {
        String addr = NettyUtil.parseChannelRemoteAddr(channel);
        // 判断通道状态是否正常
        if (!channel.isActive()) {
            log.error("channel is not active, address: {}, nick: {}", addr, nick);
            return null;
        }
        // 添加当前用户身份信息到通道数据
        //channel.attr(USER_ROOM_IN_SESSION_ATTRIBUTE_ATTR).set(room);
        //channel.attr(USER_NAME_IN_SESSION_ATTRIBUTE_ATTR).set(nick);
        // 增加一个认证用户
        accountCount.incrementAndGet();
        // 保存通讯
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(nick);
        // 保存用户到指定房间数据
        if (onlineAccount.get(room) == null) {
            ConcurrentMap<Channel, AccountInfo> userInfoConcurrentMap = new ConcurrentHashMap<>();
            onlineAccount.put(room, userInfoConcurrentMap);
        }
        onlineAccount.get(room).put(channel, accountInfo);
        // 返回数据
        return accountInfo;
    }

    /**
     * 通过用户身份获取指纹
     *
     * @param session
     * @return
     */
    public static String getRoomIdFromSession(Channel session) {
        Object attr = null;
        if (session != null) {
            // 重通道（Channel）中获取自定义的属性USER_ID_IN_SESSION_ATTRIBUTE_ATTR的属性值
            //attr = session.attr(USER_ROOM_IN_SESSION_ATTRIBUTE_ATTR).get();
            if (attr != null) {
                return (String) attr;
            }
        }
        return null;
    }

    /**
     * 通过用户身份获取指纹
     *
     * @param session
     * @return
     */
    public static String getUserNameFromSession(Channel session) {
        Object attr = null;
        if (session != null) {
            // 重通道（Channel）中获取自定义的属性USER_ID_IN_SESSION_ATTRIBUTE_ATTR的属性值
            //attr = session.attr(USER_NAME_IN_SESSION_ATTRIBUTE_ATTR).get();
            if (attr != null) {
                return (String) attr;
            }
        }
        return null;
    }

    /**
     * 从缓存中移除Channel，并且关闭Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        try {
            log.warn("channel will be remove, address is :{}", NettyUtil.parseChannelRemoteAddr(channel));
            rwLock.writeLock().lock();
            channel.close();
            // 获取通道房间数据
            String room = getRoomIdFromSession(channel);
            AccountInfo userInfo = onlineAccount.get(room).get(channel);
            if (userInfo != null) {
                AccountInfo tmp = onlineAccount.get(room).remove(channel);
                if (tmp != null && tmp.getAuth()) {
                    // 减去一个认证用户
                    accountCount.decrementAndGet();
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    /**
     * 广播普通消息
     *
     * @param message
     */
    public static void broadcastMess(int uid, String nick, String room, String message) {
        if (!BlankUtil.isBlank(message)) {
            try {
                rwLock.readLock().lock();
                Set<Channel> keySet = onlineAccount.get(room).keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = onlineAccount.get(room).get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }
                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildMessProto(uid, nick, message)));
                }
            } finally {
                rwLock.readLock().unlock();
            }
        }
    }

    /**
     * 广播系统消息
     */
    public static void broadCastInfo(int code, Object mess) {
        try {
            rwLock.readLock().lock();
            // 获取所有的通道发送数据
            Collection<ConcurrentMap<Channel, AccountInfo>> collection = onlineAccount.values();
            for (ConcurrentMap<Channel, AccountInfo> userInfos : collection) {
                Set<Channel> keySet = userInfos.keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = userInfos.get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }
                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildSystProto(code, mess)));
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }


    /**
     * 广播系统消息
     */
    public static void broadCastInfo(int code, String room, Object mess) {
        try {
            rwLock.readLock().lock();
            // 获取所有的通道发送数据
            Set<Channel> keySet = onlineAccount.get(room).keySet();
            for (Channel ch : keySet) {
                AccountInfo accountInfo = onlineAccount.get(room).get(ch);
                if (accountInfo == null || !accountInfo.getAuth()) {
                    continue;
                }
                ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildSystProto(code, mess)));
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 广播PING消息查询失效通道
     */
    public static void broadCastPing() {
        try {
            rwLock.readLock().lock();
            log.info("broadCastPing userCount: {}", accountCount.intValue());
            // 获取所有的通道发送数据
            Collection<ConcurrentMap<Channel, AccountInfo>> collection = onlineAccount.values();
            for (ConcurrentMap<Channel, AccountInfo> accountInfos : collection) {
                Set<Channel> keySet = accountInfos.keySet();
                for (Channel ch : keySet) {
                    AccountInfo accountInfo = accountInfos.get(ch);
                    if (accountInfo == null || !accountInfo.getAuth()) {
                        continue;
                    }
                    ch.writeAndFlush(new TextWebSocketFrame(MessageProto.buildPingProto()));
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 发送系统消息
     *
     * @param code
     * @param mess
     */
    public static void sendInfo(Channel channel, int code, Object mess) {
        channel.writeAndFlush(new TextWebSocketFrame(MessageProto.buildSystProto(code, mess)));
    }

    /**
     * 发送心跳数据
     *
     * @param channel 需要发送的通道
     */
    public static void sendPong(Channel channel) {
        channel.writeAndFlush(new TextWebSocketFrame(MessageProto.buildPongProto()));
    }

    /**
     * 扫描并关闭失效的Channel
     */
    public static void scanNotActiveChannel() {
        // 所有通道
        Collection<ConcurrentMap<Channel, AccountInfo>> collection = onlineAccount.values();
        for (ConcurrentMap<Channel, AccountInfo> accountInfos : collection) {
            Set<Channel> keySet = accountInfos.keySet();
            for (Channel ch : keySet) {
                // 创建者
                AccountInfo accountInfo = accountInfos.get(ch);
                if (accountInfo == null) {
                    continue;
                }
                // 判断通道状态
                if (!ch.isOpen() || !ch.isActive() || (!accountInfo.getAuth() &&
                        // 过期时间（10秒）
                        (System.currentTimeMillis() - accountInfo.getTime()) > 10000)) {
                    // 移除通道
                    removeChannel(ch);
                }
            }
        }
    }

    /**
     * 通过通道获取创建者信息
     *
     * @param channel 通道
     * @return
     */
    public static AccountInfo getAccountInfo(Channel channel) {
        String room = getRoomIdFromSession(channel);
        return onlineAccount.get(room).get(channel);
    }

    /**
     * 获取存储的所有用户及通讯数据
     *
     * @return
     */
    public static ConcurrentMap<Channel, AccountInfo> getUserInfos() {
        ConcurrentMap<Channel, AccountInfo> userInfos = new ConcurrentHashMap<>();
        Collection<ConcurrentMap<Channel, AccountInfo>> collection = onlineAccount.values();
        for (ConcurrentMap<Channel, AccountInfo> userInfoConcurrentMap : collection) {
            userInfos.putAll(userInfoConcurrentMap);
        }
        return userInfos;
    }

    /**
     * 获取在线人数信息
     *
     * @return
     */
    public static int getAuthUserCount() {
        return accountCount.get();
    }

    /**
     * 获取指定房间在线人数信息
     *
     * @return
     */
    public static int getAuthUserCount(String room) {
        return onlineAccount.get(room).size();
    }

    /**
     * 更新用户过期信息
     *
     * @param channel 通道
     */
    public static void updateUserTime(Channel channel) {
        AccountInfo accountInfo = getAccountInfo(channel);
        if (accountInfo != null) {
            //accountInfo.setTime(System.currentTimeMillis());
        }
    }
}
