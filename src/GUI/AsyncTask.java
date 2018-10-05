package GUI;

import javafx.application.Platform;

public abstract class AsyncTask<T1, T2, T3> {

    private boolean daemon = true;

    private T1[] params;

    public abstract void onPreExecute();

    public abstract T3 doInBackground(T1... params) throws InterruptedException;

    public abstract void onPostExecute(T3 params);

    public abstract void progressCallback(T2... params);

    public abstract void onFail(Exception e);

    public void publishProgress(final T2... progressParams) {
        Platform.runLater(() -> progressCallback(progressParams));
    }

    public final Thread backGroundThread = new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                final T3 param = doInBackground(params);
                Platform.runLater(() -> onPostExecute(param));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    onFail(e);
                });
                e.printStackTrace();
            }
        }
    });

    public void execute(final T1... params) {
        this.params = params;
        Platform.runLater(() -> {

            onPreExecute();

            backGroundThread.setDaemon(daemon);
            backGroundThread.start();
        });
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public final boolean isInterrupted() {
        return this.backGroundThread.isInterrupted();
    }

    public final boolean isAlive() {
        return this.backGroundThread.isAlive();
    }

    public final void terminate()
    {
        if (this.backGroundThread != null && this.backGroundThread.isAlive())
        {
            this.backGroundThread.interrupt();
        }
    }

}