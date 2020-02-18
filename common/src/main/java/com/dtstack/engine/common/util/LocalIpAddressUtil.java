package com.dtstack.engine.common.util;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

/**
 *
 * @author sishu.yss
 *
 */
public class LocalIpAddressUtil {

    /**
     * 获取本地ip地址，有可能会有多个地址, 若有多个网卡则会搜集多个网卡的ip地址
     */
    public static Set<InetAddress> resolveLocalAddresses() {
        Set<InetAddress> addrs = new HashSet<InetAddress>();
        Enumeration<NetworkInterface> ns = null;
        try {
            ns = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            // ignored...
        }
        while (ns != null && ns.hasMoreElements()) {
            NetworkInterface n = ns.nextElement();
            Enumeration<InetAddress> is = n.getInetAddresses();
            while (is.hasMoreElements()) {
                InetAddress i = is.nextElement();
                if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
                        && !isSpecialIp(i.getHostAddress())) {addrs.add(i);}
            }
        }
        return addrs;
    }

    public static List<String> resolveLocalIps() {
        Set<InetAddress> addrs = resolveLocalAddresses();
        List<String> ret = Lists.newArrayList();
        for (InetAddress addr : addrs){
            String ar = addr.getHostAddress();
            if(!ret.contains(ar)){
                ret.add(ar);
            }
        }
        return ret;
    }

    public static String getLocalHostName(){
        try{
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        }catch(Exception e){

        }
        return "unknown";
    }

    public static String getLocalAddress(){
        List<String> ls = resolveLocalIps();
        if(ls.size()==0) {
            return "127.0.0.1";
        }
        return ls.get(0);
    }

    private static boolean isSpecialIp(String ip) {
        if (ip.contains(":")) {
            return true;
        }
        if (ip.startsWith("127.")) {
            return true;
        }
        if (ip.startsWith("169.254.")) {
            return true;
        }
        if (("255.255.255.255").equals(ip)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args){
        resolveLocalIps();
    }
}