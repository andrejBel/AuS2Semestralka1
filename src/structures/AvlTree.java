package structures;

import java.util.*;

public class AvlTree<T> implements Iterable<T> {

    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator();
    }

    private static class Counter {
        long leftRotate = 0;
        long rightRotate = 0;
        long rightLeftRotate = 0;
        long leftRightRotate = 0;

        public void reset() {
            leftRotate = 0;
            rightRotate = 0;
            rightLeftRotate = 0;
            leftRightRotate = 0;
        }

        @Override
        public String toString() {
            return "Counter{" +
                    "leftRotate=" + leftRotate +
                    ", rightRotate=" + rightRotate +
                    ", rightLeftRotate=" + rightLeftRotate +
                    ", leftRightRotate=" + leftRightRotate +
                    '}';
        }
    }

    private static class Stack<T> {
        private ArrayList<T> list_;

        public Stack(int initialCapacity) {
            list_ = new ArrayList<>(initialCapacity);
        }

        public Stack() {
            list_ = new ArrayList<>(10);
        }

        public void push(T data) {
            list_.add(data);
        }

        public T pop() {
            return list_.remove(list_.size() - 1);
        }

        public void clear() {
            list_.clear();
        }

        public int size() {
            return list_.size();
        }

        public T peek() {
            return list_.get(list_.size() - 1);
        }

        public T get(int index) {
            return list_.get(index);
        }

    }

    private AvlTreeNode<T> root_;
    private Comparator<T> comparator_;
    private long size_;
    private Stack<AvlTreeNode<T>> stack_;
    private Counter counter_;

    public AvlTree(Comparator<T> comparator) {
        root_ = null;
        comparator_ = comparator;
        stack_ = new Stack<>(Byte.MAX_VALUE);
        counter_ = new Counter();
    }

    public AvlTreeNode<T> getRoot() {
        return root_;
    }

    public long getSize() {
        return size_;
    }

    public void clear() {
        root_ = null;
        size_ = 0;
        counter_.reset();
        stack_.clear();
    }

    private boolean isEqual(T first, T second) {
        return comparator_.compare(first, second) == 0;
    }

    private boolean isSmaller(T first, T second) {
        return comparator_.compare(first, second) < 0;
    }

    private boolean isLargerOrEqual(T first, T second) {
        return comparator_.compare(first, second) >= 0;
    }

    private boolean isSmallerOrEqual(T first, T second) {
        return comparator_.compare(first, second) <= 0;
    }

    private boolean isLarger(T first, T second) {
        return comparator_.compare(first, second) > 0;
    }


    public T findData(T data) {
        AvlTreeNode<T> result = trySearchData(data);
        return result == null ? null : result.getData();
    }

    private AvlTreeNode<T> trySearchData(T data) {
        AvlTreeNode<T> node = root_;
        while (node != null) {
            if (isEqual(data, node.getData())) {
                return node;
            } else if (isSmaller(data, node.getData())) {
                node = node.getLeftSon();
            } else {
                node = node.getRightSon();
            }
        }
        return null;
    }

    public boolean insert(T data) {
        boolean inserted = insertDataIntoTree(data);
        size_ += inserted ? 1 : 0;
        return inserted;
    }

    private boolean insertDataIntoTree(T data) {
        if (root_ == null) {
            root_ = new AvlTreeNode<>(data);
        } else {
            boolean found = getPathToElement(data);
            if (found) {
                return false;
            }
            // ak nenajdem, mozem vkladat
            AvlTreeNode<T> lastNodeOnPath = stack_.peek();
            AvlTreeNode<T> nodeToInsert = new AvlTreeNode<>(data);

            if (isSmaller(data, lastNodeOnPath.getData())) {
                lastNodeOnPath.setLeftSon(nodeToInsert);
            } else {
                lastNodeOnPath.setRightSon(nodeToInsert);
            }


            int lastIndex = stack_.size() - 1; // zaciname od parenta pridanej node
            byte balanceFactor = 0;

            AvlTreeNode<T> currentNode = null;
            AvlTreeNode<T> parentOfCurrentNode = null;
            boolean isCurrentNodeLeftChild = false;

            while (lastIndex >= 0) {
                currentNode = stack_.get(lastIndex);
                currentNode.updateHeight(); // update vysky

                balanceFactor = currentNode.getBalance();
                if (balanceFactor == 0) {
                    break; // mame vybalancovane, vyska stromu sa nemeni
                }
                // ak je balance +1 or -1 potom pokracujeme hore
                if (balanceFactor > 1 || balanceFactor < -1) { // treba rotaciu
                    parentOfCurrentNode = lastIndex > 0 ? stack_.get(lastIndex - 1) : null;

                    if (parentOfCurrentNode != null) {
                        isCurrentNodeLeftChild = currentNode.isLeftSon(parentOfCurrentNode);
                    }
                    rebalance(currentNode, parentOfCurrentNode, isCurrentNodeLeftChild);
                    break;
                }
                --lastIndex;
            }
        }
        return true;
    }


    private void rebalance(AvlTreeNode<T> nodeToBalance, AvlTreeNode<T> parent, boolean isLeftChildOfParent) {
        byte balanceFactorNode  = nodeToBalance.getBalance();
        AvlTreeNode<T> childOfNode = null;
        AvlTreeNode<T> newRootAfterRotation = null;
        byte balanceFactorChildNode = 0;
        if (balanceFactorNode == -2) { // ideme vyvazovat zlava
            childOfNode = nodeToBalance.getLeftSon();
            balanceFactorChildNode = childOfNode.getBalance();
            if (balanceFactorChildNode > 0) {
                newRootAfterRotation = rotateLeftRight(nodeToBalance, childOfNode);
            } else {
                newRootAfterRotation = rotateRight(nodeToBalance, childOfNode);
            }
        } else if (balanceFactorNode == 2) {
            childOfNode = nodeToBalance.getRightSon();
            balanceFactorChildNode = childOfNode.getBalance();
            if (balanceFactorChildNode < 0) {
                newRootAfterRotation = rotateRightLeft(nodeToBalance, childOfNode);
            } else {
                newRootAfterRotation = rotateLeft(nodeToBalance, childOfNode);
            }
        } else {
            System.out.println("Tu sa nikdy nedostanes, balance fasctor nie je 2 alebo -2");
            throw new RuntimeException("Balance factor je vacsi ako 2 alebo - 2");
        }

        if (parent != null) {
            if (isLeftChildOfParent) {
                parent.setLeftSon(newRootAfterRotation);
            } else {
                parent.setRightSon(newRootAfterRotation);
            }
        } else {
            root_ = newRootAfterRotation;
        }
    }

    // true if found
    // false if not found
    // naplna clensku premennu stack_
    private boolean getPathToElement(T data) {
        stack_.clear();
        AvlTreeNode<T> node = root_;
        while (node != null) {
            stack_.push(node);
            if (isEqual(data, node.getData())) {
                return true;
            } else if (isSmaller(data, node.getData())) {
                node = node.getLeftSon();
            } else {
                node = node.getRightSon();
            }
        }
        return false;
    }

    private AvlTreeNode<T> rotateRight(AvlTreeNode<T> parent, AvlTreeNode<T> child) {
        counter_.rightRotate++;
        AvlTreeNode<T> root = child;
        AvlTreeNode<T> rightChildOfChild = child.getRightSon();

        child.setRightSon(parent);
        parent.setLeftSon(rightChildOfChild);

        parent.updateHeight();
        child.updateHeight();

        return root;
    }

    private AvlTreeNode<T> rotateLeft(AvlTreeNode<T> parent, AvlTreeNode<T> child) {
        counter_.leftRotate++;
        AvlTreeNode<T> root = child;
        AvlTreeNode<T> leftChildOfChild = child.getLeftSon();

        child.setLeftSon(parent);

        parent.setRightSon(leftChildOfChild);

        parent.updateHeight();
        child.updateHeight();

        return root;
    }

    private AvlTreeNode<T> rotateRightLeft(AvlTreeNode<T> parent, AvlTreeNode<T> child) {
        counter_.rightRotate--;
        counter_.leftRotate--;
        counter_.rightLeftRotate++;
        AvlTreeNode<T> rootAfterRightRotation = rotateRight(child, child.getLeftSon());
        parent.setRightSon(rootAfterRightRotation);
        return rotateLeft(parent, rootAfterRightRotation);

    }

    private AvlTreeNode<T> rotateLeftRight(AvlTreeNode<T> parent, AvlTreeNode<T> child) {
        counter_.rightRotate--;
        counter_.leftRotate--;
        counter_.leftRightRotate++;
        AvlTreeNode<T> rootAfterLeftRotation = rotateLeft(child, child.getRightSon());
        parent.setLeftSon(rootAfterLeftRotation);
        return rotateRight(parent, rootAfterLeftRotation);
    }

    public void resetCounter() {
        counter_.reset();
    }

    public void printCounter() {
        System.out.println(counter_);
    }

    public boolean remove(T data)
    {
        boolean removed = removeElementFromTree(data);
        size_ -= removed ? 1 : 0;
        return removed;
    }


    private boolean removeElementFromTree(T data) {
        boolean found = getPathToElement(data);
        // ak ho najdem, tak mazem

        if (found) {
            extractNode();
        }

        return found;
    }

    // metoda pracujuca s obsahom stacku
    private void extractNode() {
        int indexInStack = stack_.size() - 1; // index na mazany node
        AvlTreeNode<T> node = stack_.peek(); // v node zostane smernik na mazany prvok
        AvlTreeNode<T> parent = indexInStack > 0 ? stack_.get(indexInStack - 1) : null; // otec mazaneho prvku
        if (parent == null) { // koren
            if (node.isLeaf()) {
                root_ = null;
                return;
            } else if (node.getNumberOfSons() == 1) {
                if (node.hasLeftSon()) {
                    root_ = node.getLeftSon();
                } else {
                    root_= node.getRightSon();
                }
                return;
            }
        } else {
            if (node.isLeaf()) {
                if (node.isLeftSon(parent)) {
                    parent.setLeftSon(null);
                } else {
                    parent.setRightSon(null);
                }
            } else if (node.getNumberOfSons() == 1) {
                extractNodeWithOneChildren(node, parent);
            }
        }
        if (node.getNumberOfSons() == 2) {
            AvlTreeNode<T> inorderSuccesor = node.getRightSon();
            parent = node;
            stack_.push(inorderSuccesor);
            while (inorderSuccesor.getLeftSon() != null) {
                parent = inorderSuccesor;
                inorderSuccesor = inorderSuccesor.getLeftSon();
                stack_.push(inorderSuccesor);
            }
            node.setData_(inorderSuccesor.getData());
            extractNodeWithOneChildren(inorderSuccesor, parent);
            //node = inorderSuccesor;
        }
        // mame vymazane, mozeme rebalancovat

        // ideme cestou az spat ku korenu a vyvazujeme

        stack_.pop(); // vyhodim mazany prvok, cim ho moze GC odstranit
        indexInStack = stack_.size() - 1; //balancujeme od konca
        byte balanceFactorNode = 0;
        boolean isNodeLeftChild = false;

        while (indexInStack >= 0) {
            node = stack_.get(indexInStack);
            node.updateHeight();
            balanceFactorNode = node.getBalance();
            if (balanceFactorNode == 1 || balanceFactorNode == -1) { // predtym bolo 0 a ked sa nam balance zmenmil o 1, nenarusi to avl strom, vyska zostava rovnaka
                break;
            }
            if (balanceFactorNode > 1 || balanceFactorNode < -1) { // nie je balancovana vetva
                parent = indexInStack > 0 ? stack_.get(indexInStack - 1) : null; // otec vyvazovaneho prvku
                if (parent != null) {
                    isNodeLeftChild = node.isLeftSon(parent);
                }
                rebalance(node, parent, isNodeLeftChild);
            }
            --indexInStack;
        }
    }

    private void extractNodeWithOneChildren(AvlTreeNode<T> nodeToRemove, AvlTreeNode<T> parentOfNode) {
        if (nodeToRemove.getNumberOfSons() > 1) {
            System.out.println("nemozes mazaet 2 deti");
            throw new RuntimeException("nemozes mazaet 2 deti");
        }
        AvlTreeNode<T> childOfDeletedElement = nodeToRemove.hasLeftSon() ? nodeToRemove.getLeftSon() : nodeToRemove.getRightSon();
        if (nodeToRemove.isLeftSon(parentOfNode)) {
            parentOfNode.setLeftSon(childOfDeletedElement);
        }  else {
            parentOfNode.setRightSon(childOfDeletedElement);
        }
    }
    
    private void traverseInOrderStack(AvlTreeNode<T> root, List<T> storageList) {
        if (root == null)
            return;
        stack_.clear();
        AvlTreeNode<T> current = root;

        while (current != null || stack_.size() > 0) {
            while (current != null) {
                stack_.push(current);
                current = current.getLeftSon();
            }

            current = stack_.pop();
            //System.out.println(current.getData());
            storageList.add(current.getData());
            current = current.getRightSon();
        }

    }

    private void traverseLevelOrder(AvlTreeNode<T> current, List<List<T>> storageList) {
        storageList.clear();
        LinkedList<AvlTreeNode<T>> queue = new LinkedList<>();
        if (current != null) {
            queue.push(current);
        }
        AvlTreeNode<T> node = null;
        while (queue.size() > 0) {
            LinkedList<T> resultLevel = new LinkedList<>();
            int nodeSize = queue.size();
            while (nodeSize > 0) {
                node = queue.pop();
                resultLevel.add(node.getData());
                if (node.hasLeftSon()) {
                    queue.add(node.getLeftSon());
                }
                if (node.hasRightSon()) {
                    queue.add(node.getRightSon());
                }
                --nodeSize;
            }
            storageList.add(resultLevel);
        }
    }

    public void getInterval(T from, T to, List<T> storageList) {
        boolean isDataLargerAsFrom = false;
        boolean isDataSmallerAsTo = false;
        boolean dataInRange = false;
        if (root_ == null) {
            return;
        }
        Stack<AvlTreeNode<T>> stack = new Stack<>();
        AvlTreeNode<T> current = root_;

        while (current != null || stack.size() > 0) {
            while (current != null) {
                stack.push(current);
                isDataLargerAsFrom = isLarger(current.getData(), from);
                if (isDataLargerAsFrom) {
                    current = current.getLeftSon();
                } else {
                    break;
                }
            }

            current = stack.pop();
            dataInRange = isLargerOrEqual(current.getData(), from) && isSmallerOrEqual(current.getData(), to);
            if (dataInRange) {
                storageList.add(current.getData());
            }
            isDataSmallerAsTo = isSmaller(current.getData(), to);
            if (isDataSmallerAsTo) {
                current = current.getRightSon();
            } else {
                current = null;
            }
        }
    }

    public static <T> void TraverseTreeLevelOrder(AvlTree<T> tree, List<List<T>> storageList) {
        tree.traverseLevelOrder(tree.getRoot(), storageList);
    }

    public static <T> void TraverseTree(AvlTree<T> tree, List<T> storageList) {
        tree.traverseInOrderStack(tree.getRoot(), storageList);
    }

    public boolean checkAvlTreeConditions() {
        if (root_ == null) {
            return true;
        }
        Stack<AvlTreeNode<T>> stack1 = new Stack<>(Byte.MAX_VALUE);
        Stack<AvlTreeNode<T>> stack2 = new Stack<>(Byte.MAX_VALUE);

        AvlTreeNode<T> currentNode = null;

        stack1.push(root_);
        while (stack1.size() > 0) {
            currentNode = stack1.pop();
            stack2.push(currentNode);
            if (currentNode.hasLeftSon()) {
                stack1.push(currentNode.getLeftSon());
            }
            if (currentNode.hasRightSon()) {
                stack1.push(currentNode.getRightSon());
            }
        }
        while (stack2.size() > 0) {
            currentNode = stack2.pop();
            currentNode.updateHeight();
            byte balance = currentNode.getBalance();
            if (balance > 1 || balance < -1) {
                return false;
            }
        }
        return true;
    }

    private class InOrderIterator implements Iterator<T> {

        private final Stack<AvlTreeNode<T>> stack_;

        public InOrderIterator() {
            stack_ = new Stack<>();
            stack_.push(root_);
        }

        @Override
        public boolean hasNext() {
            return stack_.size() > 1 || stack_.peek() != null;
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException("No more nodes remain to iterate");
            AvlTreeNode<T> current = stack_.pop();
            if (current != null || stack_.size() > 0) {
                while (current != null) {
                    stack_.push(current);
                    current = current.getLeftSon();
                }
                current = stack_.pop();
                stack_.push(current.getRightSon());
            }
            return current.getData();
        }
    }

}