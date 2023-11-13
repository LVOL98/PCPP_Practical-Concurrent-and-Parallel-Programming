package broadcaster;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.Random;

public class User extends AbstractBehavior<User.UserCommand> {
    /* --- Messages ------------------------------------- */
    public interface UserCommand { }

    public static final class BroadcastMessage implements UserCommand {
		public final ActorRef<UserCommand> ref;
		public final String message;
	
		public BroadcastMessage(ActorRef<UserCommand> ref, String message) {
			this.ref = ref;
			this.message = message;
		}
    }

    public static final class SubscriptionOK implements UserCommand { }

    public static final class UnsubscriptionOK implements UserCommand { }

    public static final class UserStarter implements UserCommand { }

    

    
    /* --- State ---------------------------------------- */
    private final ActorRef<Chatroom.ChatroomCommand> chatroom;

    
    /* --- Constructor ---------------------------------- */
    private User(ActorContext<UserCommand> context,
				 ActorRef<Chatroom.ChatroomCommand> chatroom) {
		super(context);
		this.chatroom = chatroom;
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<UserCommand>
		create(ActorRef<Chatroom.ChatroomCommand> chatroom) {
		return Behaviors.setup((context) -> new User(context,chatroom));
    }

    
    /* --- Message handling ----------------------------- */
    @Override
    public Receive<UserCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(BroadcastMessage.class, this::onBroadcastMessage)
			.onMessage(SubscriptionOK.class, this::onSubscriptionOK)
			.onMessage(UnsubscriptionOK.class, this::onUnsubscriptionOK)
			.onMessage(UserStarter.class, this::onUserStarter)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<UserCommand> onBroadcastMessage(BroadcastMessage msg) {
		getContext().getLog().info("{}: Message '{}' by {} received",
								   getContext().getSelf().path().name(),
								   msg.message,
								   msg.ref.path().name());
		return this;
    }

    public Behavior<UserCommand> onSubscriptionOK(SubscriptionOK msg) {
		getContext().getLog().info("{}: Subscription confirmed by chatroom",
								   getContext().getSelf().path().name());

		// Randomly decide whether the actor sends a message to broadcast
		if (new Random().nextBoolean()) {
			String message = "Salutations from " + getContext().getSelf().path().name();
			getContext().getLog().info("{}: send message '{}'",
									   getContext().getSelf().path().name(),
									   message);
			chatroom.tell(new Chatroom.EmitMessage(getContext().getSelf(), message));

			// unsubscribe after sending the message
			chatroom.tell(new Chatroom.Unsubscribe(getContext().getSelf()));
		}	
		return this;
    }

    public Behavior<UserCommand> onUnsubscriptionOK(UnsubscriptionOK msg) {
		getContext().getLog().info("{}: Unsubcription confirmed by chatroom",
								   getContext().getSelf().path().name());
		return this;
    }

    public Behavior<UserCommand> onUserStarter(UserStarter msg) {
		// subscribe
		chatroom.tell(new Chatroom.Subscribe(getContext().getSelf()));
		return this;
    }
}
