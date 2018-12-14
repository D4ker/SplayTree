import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
@SuppressWarnings("WeakerAccess")
public class SplayTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    // Метод, меняющий местами родителя с ребёнком
    private void rotate(Node<T> parent, Node<T> child) {
        final Node<T> gParent = findWithParent(root, null, parent.value).getSecond();
        if (gParent != null) {
            if (gParent.left == parent) {
                gParent.left = child;
            } else {
                gParent.right = child;
            }
        } else {
            root = child;
        }
        if (parent.left == child) {
            parent.left = child.right;
            child.right = parent;
        } else {
            parent.right = child.left;
            child.left = parent;
        }
    }

    // Метод, поднимающий узел node в корень дерева путём использования техники zig, zig-zig и zig-zag поворотов
    private Node<T> splay(Node<T> node) {
        if (node == root) return node;
        final Node<T> parent = findWithParent(root, null, node.value).getSecond();
        final Node<T> gParent = findWithParent(root, null, parent.value).getSecond();
        if (gParent == null) { // zig
            rotate(parent, node);
            return node;
        } else {
            if ((gParent.left == parent) == (parent.left == node)) { // zig-zig
                rotate(gParent, parent);
                rotate(parent, node);
            } else { // zig-zag
                rotate(parent, node);
                rotate(gParent, node);
            }
            return splay(node);
        }
    }

    // Метод, разделяющий дерево на два. В одном дереве все значения меньше value, а в другом — больше
    private Pair<Node<T>, Node<T>> split(T value) {
        if (root == null) return new Pair<>(null, null);
        find(value);
        if (value.compareTo(root.value) == 0) { // Если узел со значением value уже имеется в дереве
            return null;
        } else if (value.compareTo(root.value) < 0) {
            final Node<T> leftNode = root.left;
            root.left = null;
            return new Pair<>(leftNode, root);
        } else {
            final Node<T> rightNode = root.right;
            root.right = null;
            return new Pair<>(root, rightNode);
        }
    }

    @Override
    // Добавление узла в дерево
    public boolean add(T value) {
        final Pair<Node<T>, Node<T>> treePair = split(value);
        if (treePair == null) return false; // Если узел со значением value уже имеется в дереве, добавление не выполнять
        final Node<T> leftTree = treePair.getFirst();
        final Node<T> rightTree = treePair.getSecond();
        root = new Node<>(value);
        root.left = leftTree;
        root.right = rightTree;
        size++;
        return true;
    }

    // Метод, обрабатывающий удаление узла из дерева
    private void merge(Node<T> left, Node<T> right) {
        if (right == null) {
            root = left;
            return;
        }
        if (left == null) {
            root = right;
            return;
        }
        find(right, left.value);
        root.left = left;
    }

    @Override
    // Удаление узла из дерева
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        final T value = (T) o;
        if (root == null || o == null) return false;
        find(value);
        if (value.compareTo(root.value) != 0) return false;
        merge(root.left, root.right);
        size--;
        return true;
    }

    @Override
    // Проверка на наличие узла в дереве
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        final T t = (T) o;
        final Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    // Поиск узла по значению
    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    // Дополнительный метод для поиска узла по значению
    private Node<T> find(Node<T> start, T value) {
        final int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return splay(start);
        } else if (comparison < 0) {
            if (start.left == null) return splay(start);
            return find(start.left, value);
        } else {
            if (start.right == null) return splay(start);
            return find(start.right, value);
        }
    }

    // Поиск узла и его родителя
    private Pair<Node<T>, Node<T>> findWithParent(Node<T> start, Node<T> parent, T value) {
        if (start == null) return null;
        final int comparison = value.compareTo(start.value);
        final Pair<Node<T>, Node<T>> pair = new Pair<>(start, parent);
        if (comparison == 0) {
            return pair;
        } else if (comparison < 0) {
            if (start.left == null) return pair;
            return findWithParent(start.left, start, value);
        } else {
            if (start.right == null) return pair;
            return findWithParent(start.right, start, value);
        }
    }

    public class SplayTreeIterator implements Iterator<T> {

        private Node<T> current = null;

        // Нахождение следующего по возрастанию узла
        private Node<T> findNext() {
            if (current == null) {
                return minNode(root);
            }
            if (current.right != null) {
                return minNode(current.right);
            } else {
                return findPrevNextNode(root, current.value, null, true);
            }
        }

        // Вспомогательный метод для поиска предыдущего/следующего узла (необходим для метода remove)
        private Node<T> findPrevNextNode(Node<T> start, T value, Node<T> desiredNode, boolean next) {
            if (start == null) return null;
            final int comparison = value.compareTo(start.value);
            if (comparison == 0) {
                return desiredNode;
            } else if (comparison < 0) {
                if ((next && start.value.compareTo(value) > 0) || (!next && start.value.compareTo(value) < 0)) {
                    return findPrevNextNode(start.left, value, start, next);
                }
                return findPrevNextNode(start.left, value, desiredNode, next);
            } else {
                if ((next && start.value.compareTo(value) > 0) || (!next && start.value.compareTo(value) < 0)) {
                    return findPrevNextNode(start.right, value, start, next);
                }
                return findPrevNextNode(start.right, value, desiredNode, next);
            }
        }

        // Поиск минимального узла в дереве с корнем node
        private Node<T> minNode(Node<T> node) {
            if (node == null) return null;
            Node<T> currentNode = node;
            Node<T> leftNode = currentNode.left;
            while (leftNode != null) {
                currentNode = leftNode;
                leftNode = currentNode.left;
            }
            return currentNode;
        }

        @Override
        // Проверка на наличие следующего узла в дереве
        public boolean hasNext() {
            return findNext() != null;
        }

        @Override
        // Переход к следующему узлу
        public T next() {
            current = findNext();
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }

        @Override
        // Удаление текущего узла
        public void remove() {
            if (current != null) {
                final T oldValue = current.value;
                final Node<T> newCurrent = findPrevNextNode(root, oldValue, null, false);
                SplayTree.this.remove(oldValue);
                current = newCurrent;
            } else throw new NoSuchElementException();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new SplayTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    // Метод, возвращающий подмножество дерева начиная с fromElement и заканчивая toElement
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (root == null || fromElement == null || toElement == null) throw new NoSuchElementException();
        return new SplayTreeSet<>(this, fromElement, toElement);
    }

    @NotNull
    @Override
    // Метод, возвращающий подмножество дерева оканчивающимся toElement
    public SortedSet<T> headSet(T toElement) {
        if (root == null || toElement == null) throw new NoSuchElementException();
        return new SplayTreeSet<>(this, null, toElement);
    }

    @NotNull
    @Override
    // Метод, возвращающий подмножество дерева начиная с fromElement
    public SortedSet<T> tailSet(T fromElement) {
        if (root == null || fromElement == null) throw new NoSuchElementException();
        return new SplayTreeSet<>(this, fromElement, null);
    }

    @Override
    // Получение первого узла в дереве (минимального)
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    // Получение последнего узла в дереве (максимального)
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
