package flaxbeard.cyberware.api.hud;

public class NotificationHolder {
    private float time;
    private INotification notification;

    public NotificationHolder(float time, INotification notification)
    {
        this.time = time;
        this.notification = notification;
    }

    public float getCreatedTime()
    {
        return time;
    }

    public INotification getNotification()
    {
        return notification;
    }
}
