package GUI;

public abstract class SimpleTask {

    protected abstract boolean compute();

    protected abstract void onSuccess();

    protected abstract void onFail();

    public void execute() {
        if (compute()) {
            onSuccess();
        } else {
            onFail();
        }
    }

}
