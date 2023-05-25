package com.company.entity;

import com.company.util.DesUtil;
import com.company.util.MD5Util;

import java.util.HashMap;
import java.util.Random;

public class KDC {
    String kdcName ;
    private MD5Util md5;
    String client_timestamp;
    String client_username;
    String client_SPN;
    String client_userNonce;
    HashMap clients = new HashMap();
    HashMap servers = new HashMap();
    HashMap sessionKeys = new HashMap();
    String kdc_key ;
    String session_key;
    String tgt_expiration_time = "7200";//2小时
    String tgs_expiration_time = "28800";//8个小时
    String send_tgt_rep;
    HashMap client_pac = new HashMap();
    DesUtil des = new DesUtil();

    public KDC(String kdcName) {
        this.kdcName = kdcName;
        this.kdc_key = md5.getMD5Code(kdcName).replace("\\r\\n","");
    }
//    存储sessionKey
    public void add_sessionKey(String username,String sessionKEY)
    {
        sessionKeys.put(username,sessionKEY);
        System.out.println("用户pac键值对添加成功！！！"+username+","+sessionKEY);
    }
    public String get_sessionKey(Object username)
    {
        return String.valueOf(this.sessionKeys.get(username));
    }
//    服务端注册
    public  void server_init(String username,String key)
    {
        servers.put(username,key);
        System.out.println("服务端键值对添加成功！！！"+username+","+key);
    }
//    查询是否存在服务端
    public String get_server_key(Object userName)
    {
        return String.valueOf(this.servers.get(userName));
    }
//    客户注册
    public  void client_init(String userName, String key, PAC pac)
    {
        add_client(userName,key);
        add_client_pac(userName,pac);

    }
    public void add_client_pac(String userName, PAC pac)
    {
        client_pac.put(userName,pac);
        System.out.println("用户pac键值对添加成功！！！"+userName+","+pac);
    }
    public PAC get_client_pac(Object userName)
    {
        return (PAC) this.client_pac.get(userName);
    }
    //为注册用户添加键值对
    public void add_client(String userName, String key)
    {
        clients.put(userName,key);
        System.out.println("用户key键值对添加成功！！！"+userName+","+key);
    }
    //搜索是否存在用户，如果有则返回改用户的主键。
    public String get_client_key(Object userName)
    {
        return String.valueOf(this.clients.get(userName));
    }
    //获得客户端发来的申请as_req
    public String get_as_req(byte[] data, String username) throws Exception {
        if (get_client_key(username) == null)
            System.out.println("该用户不存在,拒绝访问");
        else {
//            System.out.println("输出："+get_client_key(username));
            byte[] as_req_get = DesUtil.decrypt(data, get_client_key(username));
            String[] split =new String(as_req_get).split(" ");
            this.client_timestamp = split[0];
////            对时间戳进行验证，有与没有将服务端部署到tomcat，使用验证不了
//            if (System.currentTimeMillis()/1000-Long.valueOf(this.client_timestamp)>1000)
//                System.out.println("客户端访问超时，拒绝访问");
            this.client_SPN = split[1];
            this.client_userNonce = split[2];
            this.client_username = username;
            this.session_key = MD5Util.getMD5Code(new Random().toString());
            add_sessionKey(this.client_username,this.session_key);
            this.send_tgt_rep = send_tgt_rep();
            System.out.println("解密as_asq:");
            System.out.println("session_key:"+this.session_key);
            System.out.println("client_timestamp"+this.client_timestamp);
            System.out.println("client_SPN"+this.client_SPN);
            System.out.println("client_userNonce"+this.client_userNonce);
            System.out.println("--------------------------");
//            System.out.println("加密的tgt:");
//            System.out.println(this.send_tgt_rep);
            return this.send_tgt_rep;
//        for(int i=0;i<split.length-1;i++)
//        {
//            System.out.println(split[i]);
//        }
//        System.out.println(username);
        }
        return null;
    }
    //返回票据授权票据TGT as_rep
    public String send_tgt_rep() throws Exception {
//        用user hash加密的数据
        String data01 = this.client_username +" "+ this.session_key +" "+
                this.tgt_expiration_time + " "+ this.client_userNonce;
//        用kdc hash加密的数据
        String data02 = this.client_username + " "+ this.session_key + " "+
                this.tgt_expiration_time + " "+ get_client_pac(this.client_username).group;

        byte[] send_data01 = DesUtil.encrypt(data01.getBytes(),get_client_key(this.client_username));
        byte[] send_data02 = DesUtil.encrypt(data02.getBytes(),this.kdc_key);
        String send = send_data01 +";"+ send_data02;
        return send;


    }
    //获得客户端发来的tgs请求
    public String get_tgs_req(String data) throws Exception {
        String[] split01 = data.split(";");
        String data01 = split01[0];
        String data02 = split01[1];
        byte[] data03 = DesUtil.decrypt(data02.getBytes(),this.kdc_key);
        System.out.println("kdc_key解密tgs:"+data03);
        String[] split02 = new String(data03).split(" ");
        this.client_username = split02[0];
        this.session_key = split02[1];
//        先解密通过kdc_key加密的tgt，获得其中的session_key再来解密
        byte[] data04 = DesUtil.decrypt(data01.getBytes(),this.session_key);
        System.out.println("session_key解密tgs:"+data04);
        String[] split03 = new String(data04).split(" ");
//        查询tgt中解密的username和session加密的username是否相等
//        如果相等，则client的临时凭证身份验证成功
        if(split03[0].equals(this.client_username))
        {
            System.out.println("client临时凭证身份验证成功！！！");
            this.client_timestamp = split03[1];
            this.client_SPN = split03[2];
            this.client_userNonce = split03[3];
            if(get_server_key(this.client_SPN)==null)
            {
                System.out.println("client访问的服务不存在！！！");
            }else {
//                System.out.println("cg!!!");
                String send_tgs = send_tgs_rep();
                return send_tgs;

            }

        }
        else {
            System.out.println("client身份不一致，拒绝服务！");
        }
        return null;
     }
     //发送客户端给tgs票据
    public String send_tgs_rep() throws Exception {
        String data01 =this.client_username+" "+get_sessionKey(this.client_username)
                +" "+this.tgs_expiration_time+" "+this.client_userNonce;
        String data02 = get_sessionKey(this.client_username)+" "+this.client_username
                +" "+this.tgs_expiration_time+" "+get_client_pac(this.client_username);
        byte[] data03 = DesUtil.encrypt(data01.getBytes(),get_sessionKey(this.client_username));
        byte[] data04 = DesUtil.encrypt(data02.getBytes(),get_server_key(this.client_SPN));
//        System.out.println("spn_key "+get_server_key(this.client_SPN));
        String data05 = data03+";"+data04;
        System.out.println("tgsdata01:"+data01);
        System.out.println("tgsdata02:"+data02);
        System.out.println("tgsdata03:"+data03);
        System.out.println("tgsdata04:"+data04);
        System.out.println("tgsdata05:"+data05);
        return data05;
    }

}
