package com.dtstack.engine.remote.message;

import java.io.Serializable;

/**
 * @Auther: dazhi
 * @Date: 2020/9/1 8:20 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -9071808313610178621L;
    private String identifier;

    private MessageStatue statue;

    private TargetInfo targetInfo;

    private Object transport;

    public Message(){
        statue = MessageStatue.START;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public TargetInfo getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public MessageStatue getStatue() {
        return statue;
    }

    public void setStatue(MessageStatue statue) {
        this.statue = statue;
    }

    public Object getTransport() {
        return transport;
    }

    public void setTransport(Object transport) {
        this.transport = transport;
    }

    public Object result(Class<?> returnType) {
        if (transport.getClass().equals(returnType) ) {
            return transport;
        }
        return null;
    }

    public Message ask(Object result, MessageStatue statue){
        this.transport = result;
        this.statue = statue;
        return this;
    }

    public enum MessageStatue{
        START,SENDER,RECEIVE,RESULT,ERROR

    }

}
