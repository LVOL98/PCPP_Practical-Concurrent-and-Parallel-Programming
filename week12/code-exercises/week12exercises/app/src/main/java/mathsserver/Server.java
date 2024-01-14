package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.IntStream;

import mathsserver.Task;
import mathsserver.Task.BinaryOperation;

public class Server extends AbstractBehavior<Server.ServerCommand> {
  /* --- Messages ------------------------------------- */
  public interface ServerCommand {
  }

  public static final class ComputeTasks implements ServerCommand {
    public final List<Task> tasks;
    public final ActorRef<Client.ClientCommand> client;

    public ComputeTasks(List<Task> tasks,
        ActorRef<Client.ClientCommand> client) {
      this.tasks = tasks;
      this.client = client;
    }
  }

  public static final class WorkDone implements ServerCommand {
    ActorRef<Worker.WorkerCommand> worker;

    public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
      this.worker = worker;
    }
  }

  /* --- State ---------------------------------------- */
  private class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
      this.x = x;
      this.y = y;
    }
  }

  private final List<ActorRef<Worker.WorkerCommand>> idleWorkers;
  private final List<ActorRef<Worker.WorkerCommand>> busyWorkers;
  private final List<Tuple<Task, ActorRef<Client.ClientCommand>>> pendingComputeTask;

  /* --- Constructor ---------------------------------- */
  private Server(ActorContext<ServerCommand> context, int minWorkers, int maxWorkers) {
    super(context);

    this.idleWorkers = new LinkedList();
    this.busyWorkers = new LinkedList();
    this.pendingComputeTask = new LinkedList();

    System.out.println("here: " + minWorkers);

    for (int i = 0; i < minWorkers; i++) {
      ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()), "worker-" + i);

      getContext().getLog().info("Created worker: " + worker.path().name());

      idleWorkers.add(worker);
    }
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
    return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<ServerCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(ComputeTasks.class, this::onComputeTasks)
        .onMessage(WorkDone.class, this::onWorkDone)
        // To be extended
        .build();
  }


  public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
    for (var task : msg.tasks) {
      if (idleWorkers.size() > 0) {
        ActorRef<Worker.WorkerCommand> worker = idleWorkers.get(0);
        idleWorkers.remove(0);
        busyWorkers.add(worker);
        worker.tell(new Worker.ComputeTask(task, msg.client));
      } else {
        pendingComputeTask.add(new Tuple(task, msg.client));
      }
    }

    return this;
  }

  public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
    if (pendingComputeTask.size() > 0) {
      var task = pendingComputeTask.get(0);
      pendingComputeTask.remove(0);
      msg.worker.tell(new Worker.ComputeTask(task, task.client));

      return this;
    } else {
      busyWorkers.remove(msg.worker);
      idleWorkers.add(msg.worker);

      return this;
    }
  }
}
