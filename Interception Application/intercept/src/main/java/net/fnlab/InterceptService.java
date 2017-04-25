package net.fnlab;

/**
 * Created by zzy on 17-4-17.
 */
public interface InterceptService {
    public void startIntercept(boolean isTcp, String ipAddress, int portNumber);
    public void deleteIntercept();
}
