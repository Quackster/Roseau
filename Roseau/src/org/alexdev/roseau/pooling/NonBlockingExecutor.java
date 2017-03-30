package org.alexdev.roseau.pooling;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class NonBlockingExecutor {
	
    private ExecutorService executor;
 
    public NonBlockingExecutor(ExecutorService executor) {
        this.executor = executor;
    }
 
    public <T> NonBlockingFuture<T> submitNonBlocking(final Callable<T> userTask) {
        
    	final NonBlockingFuture<T> nbFuture = new NonBlockingFuture<>();
        
        executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    T result = userTask.call();
                    nbFuture.setResult(result);
                    return result;
                } catch (Exception e) {
                    nbFuture.setFailure(e);
                    throw e;
                }
            }
        });
 
        return nbFuture;
    }
}