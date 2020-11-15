package fdaf.webapp.base;

public interface FrontEndUIUpdaterInterface {

    public void triggerNotifyUpdate();
    
    public void addWebSocket(WebSocketInterface websocket);
    
    public String getServiceUUID();
   
    public void runService();
}
