package com.tencent.wxcloudrun.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPUtil {

    public String getServerIP(HttpServletRequest request) throws SocketException {
        String serverIp = null;
        Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
        if (netInterfaces != null) {
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress)addresses.nextElement();
                    serverIp = ip.getHostAddress();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        serverIp = ip.getHostAddress();
                        break;
                    } else {
                        serverIp = null;
                    }
                    if (serverIp == null) {
                        if ((ip != null && ip instanceof Inet4Address) && !ip.getHostAddress().equals("127.0.0.1")) {
                            serverIp = ip.getHostAddress();
                            break;
                        }
                    }
                }
                if (serverIp != null) {
                    break;
                }
            }
        }
        return serverIp;
    }


    public String getIP(HttpServletRequest request){
        String ip=request.getHeader("x-forwarded-for");
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("X-Real-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getRemoteAddr();
        }
        return ip;
    }

}
