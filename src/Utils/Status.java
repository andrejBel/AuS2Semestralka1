package Utils;

public class Status {

    private String message_;
    private boolean status_;

    public Status() {
        message_ = "";
        status_ = true;
    }

    public boolean isStatusOk() {
        return status_;
    }

    public String getMessage() {
        return message_;
    }

    public void setMessage(String message) {
        this.message_ = message;
    }

    public void setStatus_(boolean status) {
        this.status_ = status;
    }
}
