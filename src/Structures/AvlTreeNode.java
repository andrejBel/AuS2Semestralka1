package Structures;

public class AvlTreeNode<T> {

    private AvlTreeNode<T> leftSon_;
    private AvlTreeNode<T> rightSon_;
    private T data_;
    private byte height_;


    public AvlTreeNode(T data) {
        this(data, null, null, (byte) 0);
    }

    public AvlTreeNode(T data, AvlTreeNode<T> leftSon, AvlTreeNode<T> rightSon, byte height) {
        this.data_ = data;
        this.leftSon_ = leftSon;
        this.rightSon_ = rightSon;
        this.height_ = height;
    }

    public byte getHeight() {
        return height_;
    }

    byte getBalance() {
        byte rightSubtreeHeight = AvlTreeNode.GetAvlTreeNodeHeight(this.rightSon_);
        byte leftSubtreeHeight = AvlTreeNode.GetAvlTreeNodeHeight(this.leftSon_);
        return (byte) (rightSubtreeHeight - leftSubtreeHeight);
    }

    public void updateHeight() {
        byte leftSubtreeHeight = AvlTreeNode.GetAvlTreeNodeHeight(this.leftSon_);
        byte rightSubtreeHeight = AvlTreeNode.GetAvlTreeNodeHeight(this.rightSon_);
        this.height_ = (byte) ((leftSubtreeHeight > rightSubtreeHeight ? leftSubtreeHeight : rightSubtreeHeight) + 1);
    }

    public byte getBalanceRecursively() {

        byte leftSonHeight = getHeightRecursively(leftSon_);
        byte rightSonHeight = getHeightRecursively(rightSon_);
        return (byte) (leftSonHeight - rightSonHeight);
    }

    public byte getHeightRecursively(AvlTreeNode<T> node) {
        return (byte) (node == null ? -1 : (1 + Math.max(getHeightRecursively(node.rightSon_), getHeightRecursively(node.leftSon_))));
    }


    public static <T> byte GetAvlTreeNodeHeight(AvlTreeNode<T> node) {
        return node == null ? -1 : node.getHeight();
    }

    public boolean hasLeftSon() {
        return leftSon_ != null;
    }

    public boolean hasRightSon() {
        return rightSon_ != null;
    }

    public AvlTreeNode<T> getLeftSon() {
        return leftSon_;
    }

    public void setLeftSon(AvlTreeNode<T> leftSon) {
        this.leftSon_ = leftSon;
    }

    public AvlTreeNode<T> getRightSon() {
        return rightSon_;
    }

    public void setRightSon(AvlTreeNode<T> rightSon) {
        this.rightSon_ = rightSon;
    }

    public T getData() {
        return data_;
    }

    public void setData_(T data) {
        this.data_ = data;
    }

    int getNumberOfSons() {
        return (leftSon_ != null && rightSon_ != null) ? 2 : (leftSon_ != null || rightSon_ != null) ? 1 : 0;
    }

    boolean isLeaf() {
        return leftSon_ == null && rightSon_ == null;
    }

    boolean isLeftSon(AvlTreeNode<T> parent) {
        return parent != null && parent.getLeftSon() == this;
    }

    boolean isRightSon(AvlTreeNode<T> parent) {
        return parent != null && parent.getRightSon() == this;
    }

    @Override
    public String toString() {
        return "{ Data = [" + data_ + "], height = [" + height_ + "], leftSon =" + (leftSon_ == null ? "null" : leftSon_.data_) + ", rightSon =" + (rightSon_ == null ? "null" : rightSon_.data_) + '}';
    }
}