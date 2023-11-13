package mobilepayment;

import java.util.ArrayList;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Bank extends AbstractBehavior<Bank.BankCommand> {
    public interface BankCommand {
    }

    public static final class Transaction implements BankCommand {
        ActorRef<Account.AccountCommand> fromAccountRef;
        ActorRef<Account.AccountCommand> toAccountRef;
        long amount;

        public Transaction(ActorRef<Account.AccountCommand> fromAccountRef,
                ActorRef<Account.AccountCommand> toAccountRef, long amount) {
            this.fromAccountRef = fromAccountRef;
            this.toAccountRef = toAccountRef;
            this.amount = amount;
        }
    }

    public static Behavior<BankCommand> create() {
        return Behaviors.setup(Bank::new);
    }

    private Bank(ActorContext<BankCommand> context) {
        super(context);

        context.getLog().info("Bank started " + context.getSelf().path().name());
    }

    @Override
    public Receive<BankCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transaction.class, this::onTransaction)
                .build();
    }

    public Behavior<BankCommand> onTransaction(Transaction transaction) {
        // getContext().getLog().info(String.format("Bank %s received transaction. Transfer %d from %s to %s",
        //         getContext().getSelf().path().name(),
        //         transaction.amount,
        //         transaction.fromAccountRef.path().name(),
        //         transaction.toAccountRef.path().name()));

        transaction.fromAccountRef.tell(new Account.Debit(-transaction.amount));
        transaction.toAccountRef.tell(new Account.Credit(transaction.amount));

        return this;
    }
}
