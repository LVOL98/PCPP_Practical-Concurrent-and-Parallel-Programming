package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Bank extends AbstractBehavior<Bank.BankCommand> {

    /* --- Messages ------------------------------------- */
    public interface BankCommand { }
    // Feel free to add message types at your convenience
    

    /* --- State ---------------------------------------- */
    // To be Implemented
    

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private Bank(ActorContext<BankCommand> context) {
	super(context);
    }
    

    /* --- Actor initial state -------------------------- */
    // To be Implemented


    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankCommand> createReceive() {
	return newReceiveBuilder()
	    // To be implemented
	    .build();
    }
    

    /* --- Handlers ------------------------------------- */
    // To be Implemented
}
