package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

// Hint: You may generate random numbers using Random::ints
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {
    public interface MobileAppCommand {
    }

    public static final class Transfer implements MobileAppCommand {
        final ActorRef<Account.AccountCommand> toAccountRef;
        final long amount;

        public Transfer(ActorRef<Account.AccountCommand> toAccountRef, long amount) {
            this.toAccountRef = toAccountRef;
            this.amount = amount;
        }
    }

    public static final class MakePayments implements MobileAppCommand {
        final ActorRef<Account.AccountCommand> toAccountRef;

        public MakePayments(ActorRef<Account.AccountCommand> toAccountRef) {
            this.toAccountRef = toAccountRef;
        }
    }

    public static Behavior<MobileApp.MobileAppCommand> create(ActorRef<Bank.BankCommand> myBankRef,
            ActorRef<Account.AccountCommand> myAccountRef) {
        return Behaviors.setup(context -> new MobileApp(context, myBankRef, myAccountRef));
    }

    private ActorRef<Bank.BankCommand> myBankRef;
    private ActorRef<Account.AccountCommand> myAccountRef;

    private MobileApp(ActorContext context, ActorRef<Bank.BankCommand> myBankRef,
            ActorRef<Account.AccountCommand> myAccountRef) {
        super(context);

        this.myBankRef = myBankRef;
        this.myAccountRef = myAccountRef;

        context.getLog().info("Mobile app {} started!", context.getSelf().path().name());
    }

    @Override
    public Receive<MobileAppCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transfer.class, this::onTransfer)
                .onMessage(MakePayments.class, this::onMakePayments)
                .build();
    }

    public Behavior<MobileAppCommand> onTransfer(Transfer transfer) {
        myBankRef.tell(new Bank.Transaction(myAccountRef, transfer.toAccountRef, transfer.amount));

        return this;
    }
    
    public Behavior<MobileAppCommand> onMakePayments(MakePayments makePayments) {
        var random = new Random();
        random.ints(100)
          .limit(100)
          .forEach(i -> {
            myBankRef.tell(new Bank.Transaction(myAccountRef, makePayments.toAccountRef, i));
          });
    
        return this;
    }
}
