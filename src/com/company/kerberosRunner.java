package com.company;

import com.company.entity.KDC;
import com.company.entity.PAC;
import com.company.entity.client;
import com.company.entity.server;

public class kerberosRunner {

    public static void main(String[] args) throws Exception {
//        创建客户端
        client client01 = new client("GZH","123456");
        client01.setSPN("Node01");
//        创建服务端
        server server01 = new server("Node01","999999");
//        创建kdc
        KDC kdc =new KDC("kdc01");
        PAC pac = new PAC("9999","Users");
//        客户端在kdc注册
        kdc.client_init(client01.getUserName(),client01.getPassword_hash(),pac);
//        服务端在kdc注册
        kdc.server_init(server01.getServer_name(),server01.getPassword_hash());
        byte[] as_req_send = client01.encrypt_for_as_req();
//        System.out.println(client01.getUserName());
        String tgt = kdc.get_as_req(as_req_send,client01.getUserName());
        System.out.println("KDC传输的加密的tgt（即as——req）:");
        System.out.println(tgt);
        String tgs_req_send = client01.decrypt_for_tgt(tgt);
        System.out.println("client传输的票据服务申请tgs_req:");
        System.out.println(tgs_req_send);
//        将tgs_req发送获得tgs_rep
        String tgs_rep= kdc.get_tgs_req(tgs_req_send);
        System.out.println("将tgs_req发送获得tgs_rep:"+tgs_rep);
//        client获得tgs_rep后发送ap_req
        String ap_req = client01.get_tgs_rep(tgs_rep);
        System.out.println(" client获得tgs_rep后发送ap_req: "+ap_req);
        server01.get_ap_req(ap_req);
    }
}
