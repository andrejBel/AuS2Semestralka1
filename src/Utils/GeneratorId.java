package Utils;

public class GeneratorId {

    private long value_;

    public GeneratorId() {
        this.value_ = 0;
    }

    public long getCurrentValue() {
        return value_;
    }

    public long getNextValue() {
        return ++value_;
    }

}
