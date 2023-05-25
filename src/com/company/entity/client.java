package com.company.entity;

import com.company.util.DesUtil;
import com.company.util.MD5Util;

import java.security.SecureRandom;
import java.util.Arrays;

public class client {
    private KDC kdc;
    private server server_one;
    private MD5Util md5;
    long timestamp = System.currentTimeMillis() / 1000;
    String userName;
    String password ;
    String password_hash;

    public client(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.password_hash = MD5Util.getMD5Code(password).replace("\\r\\n","");
    }

    String SPN;
    //    int userNonce = new Random().nextInt();
    SecureRandom userNonce = new SecureRandom();
    String session_key;
    String tgt_expiration_time;
    String tgs_expiration_time;



    public byte[] encrypt_for_as_req() throws Exception {
        String as_req =  this.timestamp+" "
                +this.SPN+" "+ this.userNonce;
        System.out.println("as_req需要加密的数据:"+new String(as_req));
        byte[] as_req_send = DesUtil.encrypt(as_req.getBytes(),this.password_hash);
        System.out.println("加密的as_req:"+ Arrays.toString(as_req_send));
        return as_req_send;

    }

    public String decrypt_for_tgt(String data) throws Exception {
        String[] split01 = data.split(";");
        String data01 = split01[0];
        String data02 = split01[1];
        byte[] user_data = DesUtil.decrypt(data01.getBytes(),this.password_hash);
//        System.out.println(user_data);
        String[] split02 = new String(user_data).split(" ");
        System.out.println("client:");
//        对KDC身份进行验证
        System.out.println(split02[0] );
        System.out.println(split02[1]);
        System.out.println(split02[2]);
        System.out.println(split02[3]);
//        &&  split02[3]== String.valueOf(this.userNonce)
        if(split02[0].equals(this.userName) && split02[3].equals(String.valueOf(this.userNonce)))
        {
            System.out.println("KDC身份验证成功！！！");
            this.session_key = split02[1];
            this.tgt_expiration_time = split02[2];
//            System.out.println(this.session_key+" "+this.tgt_expiration_time);
            return send_tgs_req(data02);
        }
        else
            System.out.println("KDC身份错误，拒绝");
        return null;
    }

//    向KDC发送范文服务请求tgs_req
    public String send_tgs_req(String data02) throws Exception {
        this.timestamp = System.currentTimeMillis()/1000;
        System.out.println(this.timestamp);
        String data03 = this.userName +" "+this.timestamp +" "+
                this.SPN +" "+this.userNonce ;
        System.out.println("用session key加密前的数据："+data03);
        byte[] data01 = DesUtil.encrypt(data03.getBytes(),this.session_key);
        String data = data01+";"+data02;
        return data;
    }
    //接受kdc发送的tgs_rep
    public String get_tgs_rep(String data) throws Exception {
        String[] split01=data.split(";");
        String data01 = split01[0];
        String data02 = split01[1];
        byte[] data03 = DesUtil.decrypt(data01.getBytes(),this.session_key);
        String[] split02=new String(data03).split(" ");
//        验证kdc的安全性
        if(split02[0].equals(this.userName) && split02[1].equals(this.session_key) && split02[3].equals(String.valueOf(this.userNonce)))
        {
            System.out.println("kdc返回的tgs_req安全！");
            this.tgs_expiration_time = split02[2];
//            向服务端server发送请求ap_req
            String ap_req = send_ap_req(data02);
            return ap_req;
        }
        else {
            System.out.println("kdc返回的tgs_req不安全！，拒绝访问");
        }
        return null;

    }
    //向服务端server发送请求ap_req
    public String send_ap_req(String data02) throws Exception {
//        产生新的时间戳
        timestamp = System.currentTimeMillis() / 1000;
        String data01 = this.userName+" "+this.timestamp;
        byte[] data03 = DesUtil.encrypt(data01.getBytes(),this.session_key);
        String data =data03 +"!"+ data02;
        return data;
    }

    public KDC getKdc() {
        return kdc;
    }

    public void setKdc(KDC kdc) {
        this.kdc = kdc;
    }

    public server getServer_one() {
        return server_one;
    }

    public void setServer_one(server server_one) {
        this.server_one = server_one;
    }

    public MD5Util getMd5() {
        return md5;
    }

    public void setMd5(MD5Util md5) {
        this.md5 = md5;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSPN() {
        return SPN;
    }

    public void setSPN(String SPN) {
        this.SPN = SPN;
    }

    public SecureRandom getUserNonce() {
        return userNonce;
    }

    public void setUserNonce(SecureRandom userNonce) {
        this.userNonce = userNonce;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }
}