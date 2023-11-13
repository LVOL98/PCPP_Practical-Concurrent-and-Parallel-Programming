package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

    /* --- Messages ------------------------------------- */
    public interface GuardianCommand { }
    // Feel free to add message types at your convenience

    /* --- State ---------------------------------------- */
    // empty


    /* --- Constructor ---------------------------------- */
    private Guardian(ActorContext<GuardianCommand> context) {
		super(context);
    }


    /* --- Actor initial state -------------------------- */
    // To be implemented
    

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<GuardianCommand> createReceive() {
		return newReceiveBuilder()
			// To be implemented
			.build();
    }


    /* --- Handlers ------------------------------------- */
    // To be implemented
}

