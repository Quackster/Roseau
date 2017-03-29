package org.alexdev.roseau.pooling;

public class NonBlockingFuture<T> {
	
    private FutureHandler<T> handler;
    private T result;
	private Throwable failure;
    private boolean isCompleted;
 
    public void setHandler(FutureHandler<T> handler) {
        this.handler = handler;
        if (isCompleted) {
            if (failure != null) handler.onFailure(failure);
            else handler.onSuccess(result); 
        }
    }
 
    void setResult(T result) {
        this.result = result;
        this.isCompleted = true;
        if (handler != null) {
            handler.onSuccess(result);
        }
    }
 
    void setFailure(Throwable failure) {
        this.failure = failure;
        this.isCompleted = true;
        if (handler != null) {
            handler.onFailure(failure);
        }
    }
    
    public T getResult() {
		return result;
    }
}