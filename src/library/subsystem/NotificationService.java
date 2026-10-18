package library.subsystem;

/** Sends messages to members (here: prints to the console). */
public class NotificationService {
    public void send(Member member, String message) {
        System.out.println("  [notify -> " + member.name() + "] " + message);
    }
}
