package com.dtstack.engine.remote.akka.node;

import akka.actor.ActorRef;

import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2020/9/1 3:33 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ClientRolesNode {

    private String path;
    private ActorRef actorRef;

    public ClientRolesNode(String path, ActorRef actorRef) {
        this.path = path;
        this.actorRef = actorRef;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    public void setActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRolesNode that = (ClientRolesNode) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(actorRef, that.actorRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, actorRef);
    }

    @Override
    public String toString() {
        return "ClientRolesNode{" +
                "path='" + path + '\'' +
                ", actorRef=" + actorRef +
                '}';
    }
}
