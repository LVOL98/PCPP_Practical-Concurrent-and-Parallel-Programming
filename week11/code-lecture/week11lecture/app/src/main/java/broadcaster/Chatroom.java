package broadcaster;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.List;
import java.util.ArrayList;

public class Chatroom extends AbstractBehavior<Chatroom.ChatroomCommand> {
    /* --- Messages ------------------------------------- */
    public interface ChatroomCommand { }

    public static final class Subscribe implements ChatroomCommand {
		public final ActorRef<User.UserCommand> ref;

		public Subscribe(ActorRef<User.UserCommand> ref) {
			this.ref = ref;
		}
    }
    
    public static final class Unsubscribe implements ChatroomCommand {
		public final ActorRef<User.UserCommand> ref;

		public Unsubscribe(ActorRef<User.UserCommand> ref) {
			this.ref = ref;
		}	
    }
    
    public static final class EmitMessage implements ChatroomCommand {
		public final ActorRef<User.UserCommand> sender;
		public final String message;

		public EmitMessage(ActorRef<User.UserCommand> sender,
						   String message) {
			this.sender  = sender;
			this.message = message;
		}
    }

    /* --- State ---------------------------------------- */
    private final List<ActorRef<User.UserCommand>> subscribers;


    /* --- Constructor ---------------------------------- */
    private Chatroom(ActorContext<ChatroomCommand> context) {
		super(context);
		this.subscribers = new ArrayList<ActorRef<User.UserCommand>>();
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<ChatroomCommand> create() {
		return Behaviors.setup(Chatroom::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<ChatroomCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(Subscribe.class, this::onSubscribe)
			.onMessage(Unsubscribe.class, this::onUnsubscribe)
			.onMessage(EmitMessage.class, this::onEmitMessage)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<ChatroomCommand> onSubscribe(Subscribe msg) {
		getContext().getLog().info("{}: Actor {} subscribed",
								   getContext().getSelf().path().name(),
								   msg.ref.path().name());
		subscribers.add(msg.ref);
		msg.ref.tell(new User.SubscriptionOK());
		return this;
    }

    public Behavior<ChatroomCommand> onUnsubscribe(Unsubscribe msg) {
		subscribers.remove(msg.ref);
		getContext().getLog().info("{}: Actor {} unsubscribed",
								   getContext().getSelf().path().name(),
								   msg.ref.path().name());
		msg.ref.tell(new User.UnsubscriptionOK());
		return this;
    }

    public Behavior<ChatroomCommand> onEmitMessage(EmitMessage msg) {
		getContext().getLog().info("{}: Broadcast of message {} by '{}' initiated",
								   getContext().getSelf().path().name(),
								   msg.message,
								   msg.sender.path().name());
		subscribers
			// concurrency within the actor! (not recommended, use actors instead)
			.parallelStream()
			// do not send the message to the sender
			.filter((subscriber) -> !subscriber.equals(msg.sender))
			// send message to others
			.forEach((subscriber) -> {
					subscriber.tell(new User.BroadcastMessage(msg.sender, msg.message));
				});
		return this;
    }
}
