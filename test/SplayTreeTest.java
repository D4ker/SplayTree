import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SplayTreeTest {

    private <T extends Comparable<T>> SortedSet<T> createTree() {
        return new SplayTree<T>();
    }

    @Test
    public void addTest() {
        final SortedSet<Integer> tree = createTree();
        tree.add(10);
        tree.add(5);
        tree.add(7);
        tree.add(10);
        assertEquals(3, tree.size());
        assertTrue("", tree.contains(5));
        tree.add(3);
        tree.add(1);
        tree.add(3);
        tree.add(4);
        assertEquals(6, tree.size());
        assertFalse(tree.contains(8));
        tree.add(8);
        tree.add(15);
        tree.add(15);
        tree.add(20);
        assertEquals(9, tree.size());
        assertTrue("", tree.contains(8));
        assertEquals(Integer.valueOf(1), tree.first());
        assertEquals(Integer.valueOf(20), tree.last());
    }

    @Test
    public void removeTest() {
        final Random random = new Random();
        for (int j = 0; j < 100; j++) {
            final ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                list.add(random.nextInt(100));
            }
            final TreeSet<Integer> treeSet = new TreeSet<>();
            final SortedSet<Integer> splayTreeSet = createTree();
            for (Integer element : list) {
                treeSet.add(element);
                splayTreeSet.add(element);
            }
            final Integer toRemove = list.get(random.nextInt(list.size()));
            treeSet.remove(toRemove);
            splayTreeSet.remove(toRemove);
            System.out.println("Removing " + toRemove + " from " + list);
            Assert.assertEquals("After removal of " + toRemove + " from " + list, treeSet, splayTreeSet);
            assertEquals(treeSet.size(), splayTreeSet.size());
        }

        // Тесты на особые случаи
        // Случай, когда пытаемся удалить элемент в пустом дереве
        final SortedSet<Integer> splayTreeSet = createTree();
        assertFalse(splayTreeSet.remove(2));
        assertEquals(new TreeSet<Integer>(), splayTreeSet);
        assertEquals(0, splayTreeSet.size());

        // Случай, когда пытаемся удалить несуществующий элемент в заполненном дереве
        splayTreeSet.add(5);
        splayTreeSet.add(12);
        splayTreeSet.add(7);
        splayTreeSet.add(2);
        final TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.add(5);
        treeSet.add(12);
        treeSet.add(7);
        treeSet.add(2);
        assertFalse(splayTreeSet.remove(4));
        assertEquals(treeSet, splayTreeSet);
        assertEquals(4, splayTreeSet.size());

        // Случай, когда пытаемся положить null как аргумент
        assertFalse(splayTreeSet.remove(null));
        assertEquals(treeSet, splayTreeSet);
        assertEquals(4, splayTreeSet.size());
    }

    @Test
    public void iteratorTest() {
        final Random random = new Random();
        for (int j = 0; j < 100; j++) {
            final ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                list.add(random.nextInt(100));
            }
            final TreeSet<Integer> treeSet = new TreeSet<>();
            final SortedSet<Integer> splayTreeSet = createTree();
            for (Integer element : list) {
                treeSet.add(element);
                splayTreeSet.add(element);
            }
            final Iterator<Integer> treeIt = treeSet.iterator();
            final Iterator<Integer> splayTreeIt = splayTreeSet.iterator();
            System.out.println("Traversing " + list);
            while (treeIt.hasNext()) {
                assertEquals(treeIt.next(), splayTreeIt.next());
            }
        }

        // Тесты на особые случаи
        // Случай, когда дерево пустое
        final SortedSet<Integer> splayTreeSet = createTree();
        final Iterator<Integer> splayTreeIt = splayTreeSet.iterator();
        assertFalse(splayTreeIt.hasNext());
        try {
            splayTreeIt.next();
            assert false;
        } catch (NoSuchElementException e) {
            System.out.println("Exception " + e + " successfully processed");
        }

        // Случай, когда итератор выходит за границы
        splayTreeSet.add(5);
        splayTreeSet.add(12);
        splayTreeSet.add(7);
        splayTreeSet.add(2);
        try {
            for (int i = 0; i <= splayTreeSet.size(); i++) {
                splayTreeIt.next();
            }
            assert false;
        } catch (NoSuchElementException e) {
            System.out.println("Exception " + e + " successfully processed");
        }
    }

    @Test
    public void iteratorRemoveTest() {
        final Random random = new Random();
        for (int j = 0; j < 100; j++) {
            final ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                list.add(random.nextInt(100));
            }
            final TreeSet<Integer> treeSet = new TreeSet<>();
            final SortedSet<Integer> splayTreeSet = createTree();
            for (Integer element : list) {
                treeSet.add(element);
                splayTreeSet.add(element);
            }
            final Integer toRemove = list.get(random.nextInt(list.size()));
            treeSet.remove(toRemove);
            System.out.println("Removing " + toRemove + " from " + list);
            final Iterator<Integer> iterator = splayTreeSet.iterator();
            while (iterator.hasNext()) {
                final Integer element = iterator.next();
                System.out.print(element + " ");
                if (element.equals(toRemove)) {
                    iterator.remove();
                }
            }
            System.out.println();
            Assert.assertEquals("After removal of " + toRemove + " from " + list, treeSet, splayTreeSet);
            assertEquals(treeSet.size(), splayTreeSet.size());
        }

        // Тесты на особые случаи
        // Случай, когда дерево пустое
        final SortedSet<Integer> splayTreeSet = createTree();
        final Iterator<Integer> splayTreeIt = splayTreeSet.iterator();
        try {
            splayTreeIt.remove();
            assert false;
        } catch (NoSuchElementException e) {
            System.out.println("Exception " + e + " successfully processed");
        }

        // Случай, когда удаляется последний элемент в дереве
        splayTreeSet.add(2);
        splayTreeIt.next();
        splayTreeIt.remove();
        try {
            splayTreeIt.remove();
            assert false;
        } catch (NoSuchElementException e) {
            System.out.println("Exception " + e + " successfully processed");
        }
        try {
            splayTreeIt.next();
            assert false;
        } catch (NoSuchElementException e) {
            System.out.println("Exception " + e + " successfully processed");
        }
    }
}
