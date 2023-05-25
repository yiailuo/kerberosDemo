package com.company.test;

import com.company.entity.client;
import com.company.util.DesUtil;

import java.nio.charset.StandardCharsets;

class clientTest {
    public static void main(String[] args) throws Exception {
        client client01 = new client("GZH","asd");
        String data = "我是易霭珞";
        System.out.println(data);
        String s1 = client01.getTimestamp() + " " + client01.getUserName() + " "
                + client01.getSPN() + " " + client01.getUserNonce();
        byte[] as_req=s1.getBytes();
        System.out.println(new String(as_req));
        byte[] as_req_send = DesUtil.encrypt(as_req,client01.getPassword_hash());
        System.out.println(new String(as_req_send));
        byte[] as_req_get = DesUtil.decrypt(as_req_send,client01.getPassword_hash());
        System.out.println(new String(as_req_get));
//        String data2 = des.encrypt(data,client01.getUser_hash());
//        System.out.println(data2);
//        System.out.println(des.decrypt(data2,client01.getUser_hash()));
//        System.out.println(client01.getUserNonce());
//        System.out.println(client01.send_as_req().getSPN());
//        client01.sss();
    }

}