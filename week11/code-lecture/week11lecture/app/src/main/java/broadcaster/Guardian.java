package broadcaster;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.stream.IntStream;

public class Guardian extends AbstractBehavior<Guardian.KickOff> {
    /* --- Messages ------------------------------------- */
    public static final class KickOff { }

    /* --- State ---------------------------------------- */
    // empty

    
    /* --- Constructor ---------------------------------- */
    private Guardian(ActorContext<KickOff> context) {
		super(context);
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<KickOff> create() {
		return Behaviors.setup(Guardian::new);
    }

    
    /* --- Message handling ----------------------------- */
    @Override
    public Receive<KickOff> createReceive() {
		return newReceiveBuilder()
			.onMessage(KickOff.class, this::onKickOff)
			.build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<KickOff> onKickOff(KickOff msg) {
		// spawn the chatroom
		final ActorRef<Chatroom.ChatroomCommand> chatroom =
			getContext().spawn(Chatroom.create(), "chatroom_actor");

		// spawn N users, and tell them to start
		final int N = 4;
		IntStream
			.range(1,N+1)
			.forEach((id) -> {
					final ActorRef<User.UserCommand> user =
						getContext().spawn(User.create(chatroom), "user_"+id);
					user.tell(new User.UserStarter());
				});
		return this;
    }
}
