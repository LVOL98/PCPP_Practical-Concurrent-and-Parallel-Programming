package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

// Hint: You may generate random numbers using Random::ints
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {

    /* --- Messages ------------------------------------- */
    public interface MobileAppCommand { }
    // Feel free to add message types at your convenience
    

    /* --- State ---------------------------------------- */
    // To be Implemented
    

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private MobileApp(ActorContext context) {	
		super(context);
		context.getLog().info("Mobile app {} started!",
							  context.getSelf().path().name());
    }
    

    /* --- Actor initial state -------------------------- */
    public static Behavior<MobileApp.MobileAppCommand> create() {
		return Behaviors.setup(MobileApp::new);
		// You may extend the constructor if necessary
    }
    

    /* --- Message handling ----------------------------- */    
    @Override
    public Receive<MobileAppCommand> createReceive() {
		return newReceiveBuilder()
			// To be implemented
			.build();
    }

    

    /* --- Handlers ------------------------------------- */
    // To be Implemented
}
