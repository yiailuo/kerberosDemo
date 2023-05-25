package com.company.entity;
//用户特权信息
public class PAC {
//    SID也就是安全标识符（Security Identifiers）
//    相当于身份证
    String sid;
    String group;
    /*
    users
    power users
    Administrators
    Guests
    Everyone
    system
     */

    public PAC(String sid, String group) {
        this.sid = sid;
        this.group = group;
    }
}
