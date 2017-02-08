package sample;

import java.net.InetAddress;

/**
 * Created by Alex on 27.01.2017.
 */
public class Member {

    private String name;
    private InetAddress address;
    private long lastAcceptedEchoTime;

    public Member(){
        lastAcceptedEchoTime = System.currentTimeMillis();
    }

    public Member(String name, InetAddress address){
        this.name = name;
        this.address = address;

        lastAcceptedEchoTime = System.currentTimeMillis();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    public long getLastAcceptedEchoTime() {
        return lastAcceptedEchoTime;
    }

    public void setLastAcceptedEchoTime(long lastAcceptedEchoTime) {
        this.lastAcceptedEchoTime = lastAcceptedEchoTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (getName() != null ? !getName().equals(member.getName()) : member.getName() != null) return false;
        return getAddress() != null ? getAddress().equals(member.getAddress()) : member.getAddress() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        return result;
    }
}
