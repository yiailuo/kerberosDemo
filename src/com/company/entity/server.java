package com.company.entity;

import com.company.util.DesUtil;
import com.company.util.MD5Util;

public class server {
    String server_name;
    String password ;
    private MD5Util md5;
    String password_hash;
    DesUtil des = new DesUtil();
    String service_session_key;
    String client_userName;
    String tgs_expiration_time;
    String client_pac;

    public server(String server_name, String password) {
        this.server_name = server_name;
        this.password = password;
        this.password_hash= MD5Util.getMD5Code(password).replace("\\r\\n","");
    }
//    获得client发来的ap_req
    public void get_ap_req(String data) throws Exception {
        String[] split01 = data.split("!");
        String data01 = split01[0];
        String data02 = split01[1];
//        System.out.println(this.password_hash);
        byte[] data03 = DesUtil.decrypt(data02.getBytes(),this.password_hash);
        System.out.println("server解密server——key加密的数据成功！"+data03);
        String[] split02 = new String(data03).split(" ");
        this.service_session_key = split02[0];
        this.client_userName = split02[1];
        this.tgs_expiration_time = split02[2];
        this.client_pac = split02[3];
//        对客户端进行验证：
//        检查时间戳是否超时，检查客户端pac权限是否够，检查tgs是否过期。
//        获得session_key再进行解密
        byte[] data04 = DesUtil.decrypt(data01.getBytes(),this.service_session_key);
        System.out.println("server解密session加密的数据成功！"+data04);
        System.out.println("接受client成功，提供服务！！！");

    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }
}
