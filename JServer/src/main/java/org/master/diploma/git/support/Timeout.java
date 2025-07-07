package org.master.diploma.git.support;

import lombok.Getter;
import lombok.Setter;
import org.master.diploma.git.support.exception.BruteForceTimeoutException;

import java.util.concurrent.TimeoutException;

@Getter
@Setter

public class Timeout {


    private long startTime = System.currentTimeMillis();

    @Getter
    private static Timeout instance;
    private Timeout() {

    }

    static {
        instance = new Timeout();
    }


    public void brokeTime(long delta){
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > delta){
            throw new BruteForceTimeoutException("Too long time for graph analyze");
        }
    }

    public void updateStartTime(){
        this.startTime = System.currentTimeMillis();
    }
}
