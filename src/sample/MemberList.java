package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Member;

/**
 * Created by Alex on 27.01.2017.
 */
public class MemberList {
    private  ObservableList<Member> members = FXCollections.observableArrayList();

    public ObservableList<Member> getMembers() {
        return members;
    }

    public Member get(int index){
        return members.get(index);
    }

    public void add(Member member){
        if (!members.contains(member)) {
            members.add(member);
        }
    }
}
