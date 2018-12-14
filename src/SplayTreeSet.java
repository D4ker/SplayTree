import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Вспомогательный класс для реализации в SplayTree интерфейса SortedSet
public class SplayTreeSet<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {

    private final SplayTree<T> tree;

    private final T start;
    private final T end;

    SplayTreeSet(SplayTree<T> tree, T start, T end) {
        this.tree = tree;
        this.start = start;
        this.end = end;
    }

    @Override
    // Добавление элемента
    public boolean add(T element) {
        if (elementInInterval(element)) return tree.add(element);
        return false;

    }

    @Override
    // Удаление элемента
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        final T element = (T) o;
        if (elementInInterval(element)) return tree.remove(element);
        return false;
    }

    // Проверка на вхождение элемента в допустимый интервал
    private boolean elementInInterval(T element) {
        if (start == null && end == null) return false;
        return (start == null || element.compareTo(start) >= 0) && (end == null || element.compareTo(end) < 0);
    }

    public class SplayTreeSetIterator implements Iterator<T> {

        private final Iterator<T> treeIt = SplayTreeSet.this.tree.iterator();

        private T next = null;

        SplayTreeSetIterator() {
            while (treeIt.hasNext()) {
                final T next = treeIt.next();
                if (elementInInterval(next)) {
                    this.next = next;
                    break;
                }
            }
        }

        @Override
        // Проверка на наличие следующего элемента
        public boolean hasNext() {
            return next != null;
        }

        @Override
        // Переход к следующему элементу
        public T next() {
            if (next == null) throw new NoSuchElementException();
            final T result = next;
            next = treeIt.hasNext() ? treeIt.next() : null;
            if (!elementInInterval(next)) next = null;
            return result;
        }

        @Override
        // Удаление текущего элемента
        public void remove() {
            treeIt.remove();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new SplayTreeSetIterator();
    }

    @Override
    // Получение текущего размера множества
    public int size() {
        int size = 0;
        for (T element : SplayTreeSet.this.tree) {
            if (elementInInterval(element)) size++;
            if (end != null && element.compareTo(end) >= 0) break;
        }
        return size;
    }

    @Override
    // Проверка на наличие элемента в множестве
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        final T element = (T) o;
        return tree.contains(o) && elementInInterval(element);
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return tree.comparator();
    }

    @NotNull
    @Override
    // Метод, возвращающий подмножество дерева начиная с fromElement и заканчивая toElement
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return new SplayTreeSet<>(tree, fromElement, toElement);
    }

    @NotNull
    @Override
    // Метод, возвращающий подмножество дерева оканчивающимся toElement
    public SortedSet<T> headSet(T toElement) {
        return new SplayTreeSet<>(tree, null, toElement);
    }

    @NotNull
    @Override
    // Метод, возвращающий подмножество дерева начиная с fromElement
    public SortedSet<T> tailSet(T fromElement) {
        return new SplayTreeSet<>(tree, fromElement, null);
    }

    @Override
    // Получение первого элемента множества (минимального)
    public T first() {
        for (T element : SplayTreeSet.this.tree) {
            if (elementInInterval(element)) return element;
        }
        throw new NoSuchElementException();
    }

    @Override
    // Получение последнего элемента множества (максимального)
    public T last() {
        T last = null;
        for (T element : SplayTreeSet.this.tree) {
            if (elementInInterval(element)) last = element;
            if (end != null && element.compareTo(end) >= 0) break;
        }
        if (last == null) throw new NoSuchElementException();
        return last;
    }
}
