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
