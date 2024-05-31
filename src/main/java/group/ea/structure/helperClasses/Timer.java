package group.ea.structure.helperClasses;

import java.util.concurrent.TimeUnit;

public class Timer {

    long timer;
    String _timee;
    public Timer(){
        timer = 0;
    }



    public void startTimer(String thingToTime){
        timer = System.currentTimeMillis();
        _timee = thingToTime;
    }

    public void endTimer(){
        timer = (System.currentTimeMillis() - timer);
        System.out.println(_timee + " takes " + timer + "ms");
    }

    public long getCurrentTimer(){
        return System.currentTimeMillis() - timer;
    }
}
