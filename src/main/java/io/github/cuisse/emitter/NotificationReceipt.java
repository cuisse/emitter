package io.github.cuisse.emitter;

/**
 * Notification receipt.
 * 
 * @param removed the number of subscribers removed
 * @param output  the output of the notification
 * @param status  the status of the notification
 * 
 * @author Brayan Roman
 * @since  1.0.0
 */
public record NotificationReceipt(
    int     removed,
    Object  output,
    Status  status
) {
    public enum Status {
        SUCCESS,
        SUCCESS_NO_SUBSCRIBERS,
        FAILED,
        FAILED_CHANNEL_NOT_FOUND
    }
}
