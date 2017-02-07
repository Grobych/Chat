package sample;

import java.net.InetAddress;

/**
 * Created by Alex on 27.01.2017.
 */
public class Member implements Comparable {
    private String name;
    private InetAddress address;

    public Member(){}
    public Member(String name, InetAddress address){
        this.name = name;
        this.address = address;
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

    @Override
    public int compareTo(Object object) {
        Member member = (Member) object;
        if (this.name.equals(member.name)&&this.address.equals(member.address))
            return 0;
        else return 1;
    }
}
