package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Member;

import java.util.Iterator;

/**
 * Created by Alex on 27.01.2017.
 */
public class MemberList {
    private  ObservableList<Member> members = FXCollections.observableArrayList();


    public MemberList(){
        Runnable lateKiller = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Sleeping for kill");
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Iterator<Member> iterator = members.iterator();
                    while (iterator.hasNext()){
                        long lastTime = iterator.next().getLastAcceptedEchoTime();
                        if (System.currentTimeMillis() - lastTime > 10* 1000){
                            System.out.println("killed");
                            iterator.remove();
                        }
                    }
                    if (!MemberThread.isWorking) break;
                }
            }
        };
        new Thread(lateKiller).start();
    }

    public ObservableList<Member> getMembers() {
        synchronized (this){
            return members;
        }
    }


    public Member get(int index){
        synchronized (this) {
            return members.get(index);
        }
    }

    public void add(Member member){
        synchronized (this){

            if (!members.contains(member)) {
                members.add(member);
            } else {
                Member existing = members.get(members.indexOf(member));
                existing.setLastAcceptedEchoTime(System.currentTimeMillis());
            }
        }
    }


}
