package boundedbuffer;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
    
	// actor system
	final ActorSystem<Guardian.KickOff> guardian =
	    ActorSystem.create(Guardian.create(), "broadcaster_system");

	// init message
	guardian.tell(new Guardian.KickOff());

	// wait until user presses enter
	try {
	    System.out.println(">>> Press ENTER to exit <<<");
	    System.in.read();
	}
	catch (IOException e) {
	    System.out.println("Error " + e.getMessage());
	    e.printStackTrace();
	} finally {
	    guardian.terminate();
	}
    
    }
    
}
