package structures;

import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.function.Consumer;

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

    private AvlTreeNode<T> trySearchData(@NotNull T data) {
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

    private boolean insertDataIntoTree(@NotNull T data) {
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
            stack_.push(nodeToInsert);
            if (isSmaller(data, lastNodeOnPath.getData())) {
                lastNodeOnPath.setLeftSon(nodeToInsert);
            } else {
                lastNodeOnPath.setRightSon(nodeToInsert);
            }



            int lastIndex = stack_.size() - 2; // zaciname az od parenta
            byte balanceFactor = 0;

            AvlTreeNode<T> currentNode = null;
            AvlTreeNode<T> childOfCurrentNodeOnPath = null;
            AvlTreeNode<T> newRootAfterRotation = null;
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

                    childOfCurrentNodeOnPath = stack_.get(lastIndex + 1);
                    parentOfCurrentNode = lastIndex > 0 ? stack_.get(lastIndex - 1) : null;
                    if (parentOfCurrentNode != null) {
                        isCurrentNodeLeftChild = currentNode.isLeftSon(parentOfCurrentNode);
                    }
                    int balanceFactorChild = childOfCurrentNodeOnPath.getBalance();

                    if (childOfCurrentNodeOnPath.isRightSon(currentNode) && balanceFactor > 1) {

                        if (balanceFactorChild < 0) {
                            newRootAfterRotation = rotateRightLeft(currentNode, childOfCurrentNodeOnPath);
                            // rotate right left
                        } else {
                            newRootAfterRotation = rotateLeft(currentNode, childOfCurrentNodeOnPath);
                            // rotate left
                        }

                    } else if (childOfCurrentNodeOnPath.isLeftSon(currentNode) && balanceFactor < -1) {

                        if (balanceFactorChild > 0) {
                            // rotate left right
                            newRootAfterRotation = rotateLeftRight(currentNode, childOfCurrentNodeOnPath);
                        } else {
                            newRootAfterRotation = rotateRight(currentNode, childOfCurrentNodeOnPath);
                            // rotate right
                        }

                    } else {
                        System.out.println("tu sa nemas ako dostat");
                    }
                    if (parentOfCurrentNode != null) {
                        if (isCurrentNodeLeftChild) {
                            parentOfCurrentNode.setLeftSon(newRootAfterRotation);
                        } else {
                            parentOfCurrentNode.setRightSon(newRootAfterRotation);
                        }
                    } else {
                        root_ = newRootAfterRotation;
                    }

                    break;
                }
                --lastIndex;
            }



        }
        return true;
    }

    // true if found
    // false if not found
    // naplna clensku premennu stack_
    private boolean getPathToElement(@NotNull T data) {
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
        int indexInStack = stack_.size() - 2; // index na rodica mazaneho prvku




        AvlTreeNode<T> node = stack_.peek();
        AvlTreeNode<T> parent = indexInStack >= 0 ? stack_.get(indexInStack) : null;
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
            } else { // 2 synovia
                AvlTreeNode<T> inorderSuccesor = node.getRightSon();
                AvlTreeNode<T> inorderSuccesorParent = node;
                stack_.push(inorderSuccesor);
                while (inorderSuccesor.getLeftSon() != null) {
                    inorderSuccesorParent = inorderSuccesor;
                    inorderSuccesor = inorderSuccesor.getLeftSon();
                    stack_.push(inorderSuccesor);
                }
                node.setData_(inorderSuccesor.getData()); // nastavim data mazanemu na inorder succsor a vymazem succesora
                extractNodeWithOneChildren(inorderSuccesor, inorderSuccesorParent);
            }
        } else {
            if (node.isLeaf()) {
                if (node.isLeftSon(parent)) {
                    parent.setLeftSon(null);
                } else {
                    parent.setRightSon(null);
                }
            } else if (node.getNumberOfSons() == 1) {
                AvlTreeNode<T> childOfDeletedElement = node.hasLeftSon() ? node.getLeftSon() : node.getRightSon();
                if (node.isLeftSon(parent)) {
                    parent.setLeftSon(childOfDeletedElement);
                }  else {
                    parent.setRightSon(childOfDeletedElement);
                }
            } else {
                AvlTreeNode<T> inorderSuccesor = node.getRightSon();
                AvlTreeNode<T> inorderSuccesorParent = node;
                stack_.push(inorderSuccesor);
                while (inorderSuccesor.getLeftSon() != null) {
                    inorderSuccesorParent = inorderSuccesor;
                    inorderSuccesor = inorderSuccesor.getLeftSon();
                    stack_.push(inorderSuccesor);
                }
                node.setData_(inorderSuccesor.getData());

                extractNodeWithOneChildren(inorderSuccesor, inorderSuccesorParent);
            }
        }
    }

    private void extractNodeWithOneChildren(AvlTreeNode<T> nodeToRemove, AvlTreeNode<T> parentOfNode) {
        if (nodeToRemove.getNumberOfSons() != 1) {
            return;
        }
    }

    private void traverseInOrderMorris(AvlTreeNode<T> root, List<T> storageList) {
        AvlTreeNode<T> current = null;
        AvlTreeNode<T> previous = null;
        if (root == null)
            return;
        current = root;
        while (current != null) {
            if (current.getLeftSon() == null) {
                //System.out.print(current.getData() + " ");
                storageList.add(current.getData());
                current = current.getRightSon();
            }
            else {
                /* Find the inorder predecessor of current */
                previous = current.getLeftSon();
                while (previous.getRightSon() != null && previous.getRightSon() != current)
                    previous = previous.getRightSon();

                /* Make current as right child of its inorder predecessor */
                if (previous.getRightSon() == null) {
                    previous.setRightSon(current);
                    current = current.getLeftSon();
                }

                /* Revert the changes made in if part to restore the
                    original tree i.e., fix the right child of predecssor*/
                else {
                    previous.setRightSon(null);
                    //System.out.print(current.getData() + " ");
                    storageList.add(current.getData());
                    current = current.getRightSon();
                } /* End of if condition pre->right == NULL */

            } /* End of if condition current->left == NULL*/

        } /* End of while */

    }

    private void traverseInOrderStack(AvlTreeNode<T> root, List<T> storageList) {
        if (root == null)
            return;
        Stack<AvlTreeNode<T>> stack = new Stack<>();
        AvlTreeNode<T> current = root;

        while (current != null || stack.size() > 0) {
            while (current != null) {
                stack.push(current);
                current = current.getLeftSon();
            }

            current = stack.pop();
            //System.out.println(current.getData());
            storageList.add(current.getData());
            current = current.getRightSon();
        }

    }

    private static <T> void traverseLevelOrder(AvlTreeNode<T> current, List<List<T>> storageList) {
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

    public static <T> void traverseTreeLevelOrder(AvlTree<T> tree, List<List<T>> storageList) {
        tree.traverseLevelOrder(tree.getRoot(), storageList);
    }

    public static <T> void traverseTree(AvlTree<T> tree, List<T> storageList) {
        tree.traverseInOrderMorris(tree.getRoot(), storageList);
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
            AvlTreeNode<T> forReturn = null;
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