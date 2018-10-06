package GUI;

public abstract class SimpleTask {

    public abstract boolean compute();

    public abstract void onSuccess();

    public abstract void onFail();

    public void execute() {
        if (compute()) {
            onSuccess();
        } else {
            onFail();
        }
    }

}
