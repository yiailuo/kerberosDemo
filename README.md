# kerberosDemo

复现kerberos的验证过程

一个安全的Hadoop集群是基于Kerberos,提供基于角色的用户认证、授权和审计功能。
在研究[Hadoop安全认证](https://blog.csdn.net/m0_52931616/article/details/126707262?spm=1001.2014.3001.5502)的过程中，
通过阅读kerberos源码（krb5-1.19.2）了解如何进行加密，再使用java编写代码进行模拟，采用的加密算法是DES
