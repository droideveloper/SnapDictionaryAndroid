package org.fs.dictionary.listener;

/**
 * Created by Fatih on 28/11/14.
 */
public interface ActionsCallback {
    
    public void onLight(int option);// camera light action
    
    public void onFocus();// camera focus action
    
    public void onZoom(int zoom);// zoom handle 
        
    public void onBack(); // return back
    
    public void onListen();// listen word

    public int maxZoom();

    public int minZoom();

    public void lookUp();
}
