package com.ppm.trace.trace;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsynExcutorService implements Executor {

    ThreadPoolExecutor threadPoolExecutor;

    public AsynExcutorService(){
        threadPoolExecutor = new ThreadPoolExecutor(10,20,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>(100));
    }

    @Override
    public void execute(Runnable command) {
        Runnable wapper = new RunableWapper(command);
        threadPoolExecutor.execute(wapper);
    }

    class RunableWapper implements Runnable{

        Runnable runnable;
        public RunableWapper(Runnable runnable){
            this.runnable = runnable;
        }

        public void before(){
            System.out.println("before");
        }

        @Override
        public void run() {
            before();
            runnable.run();
            after();
        }

        public void after(){
            System.out.println("after");
        }
    }

}

