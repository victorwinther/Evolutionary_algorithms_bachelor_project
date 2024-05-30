package group.ea.structure.helperClasses;

public class Timer {

    long timer;
    String _timee;
    public Timer(){
        timer = 0;
    }



    public void startTimer(String thingToTime){
        timer = System.nanoTime();
        _timee = thingToTime;
    }

    public void endTimer(){
        long output = timer - System.nanoTime() / 1000000;
        System.out.println(_timee + " takes " + output + "ms");
    }
}
