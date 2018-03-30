package gameoflifegui.mainpanel;

public interface MainPanelObserver {

    void startEvent();

    void stopEvent();

    void boardUpdated();
}
