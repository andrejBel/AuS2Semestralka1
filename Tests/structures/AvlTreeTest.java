package structures;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;


public class AvlTreeTest {

    AvlTree<Integer> testAvlTreeAscendant_;

    @Before
    public void initialize() {
        testAvlTreeAscendant_ = new AvlTree<>((o1, o2) -> o1 - o2);  // 1 < 2
    }

    @Test
    public void testAvlTreeIterator() {
        ArrayList<Integer> listOfValues = new ArrayList<>();
        int from = -10000;
        int to = 10000;

        for (int i = from; i < to; ++i ) {
            listOfValues.add(i);
            boolean inserted  = testAvlTreeAscendant_.insert(i);
            Assert.assertTrue("Nepodarilo a vlozit", inserted);

        }
        int index = 0;
        for (Integer num : testAvlTreeAscendant_) {
            Assert.assertEquals(listOfValues.get(index), num);
            ++index;
        }
    }

    @Test
    public void testAvlTreeRightRotate() {
        for (int i = 0; i < 10000000; i++) {
            Assert.assertTrue(testAvlTreeAscendant_.insert(i));
        }

    }

    @Test
    public void testAvlTreeLeftRotate() {

    }

    @Test
    public void testAvlTreeRightLeftRotate() {

    }

    @Test
    public void testAvlTreeLeftRightRotate() {

    }


    @Test
    public void testAvlTreeInsertion() {
        ArrayList<Integer> listOfValues = new ArrayList<>();
        int from = -10000;
        int to = 10000;

        for (int i = from; i < to; ++i ) {
            listOfValues.add(i);
            boolean inserted  = testAvlTreeAscendant_.insert(i);
            Assert.assertTrue("Nepodarilo a vlozit", inserted);
        }
        ArrayList<Integer> resultList = new ArrayList<>();
        AvlTree.traverseTree(testAvlTreeAscendant_, resultList);
        for (int i = 0; i < listOfValues.size(); ++i ) {
            Assert.assertEquals(listOfValues.get(i), resultList.get(i));
        }
    }

    @Test
    public void testRandomInsertion() {
        for (int k = 0; k < 10; k++) {
            testAvlTreeAscendant_.clear();
            ArrayList<Integer> insertedValues = new ArrayList<>();
            Random generator = new Random();


            int iterations = 100;

            for (int i = 0; i < iterations; i++) {
                int generated = generator.nextInt(iterations);
                boolean found = false;
                for (Integer num: insertedValues) {
                    if (num == generated) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    insertedValues.add(generated);
                    boolean inserted = testAvlTreeAscendant_.insert(generated);
                    Assert.assertTrue("Nepodarilo a vlozit", inserted);
                }
            }
            insertedValues.sort((o1, o2) -> o1 - o2);

            ArrayList<Integer> resultList = new ArrayList<>();
            AvlTree.traverseTree(testAvlTreeAscendant_, resultList);
            for (int i = 0; i < insertedValues.size(); ++i) {
                Assert.assertEquals(insertedValues.get(i), resultList.get(i));
            }
            System.out.println("Result list size: " + resultList.size());
        }
    }

    @Test
    public void testRandomRemove() {
        ArrayList<Integer> insertedValues = new ArrayList<>();
        Random generator = new Random();
        int iterations = 10000;

        for (int i = 0; i < iterations; i++) {
            int generated = generator.nextInt(iterations * 10);
            boolean found = false;
            for (Integer num: insertedValues) {
                if (num == generated) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                //System.out.println("generated: " + generated);
                insertedValues.add(generated);
                boolean inserted = testAvlTreeAscendant_.insert(generated);
                Assert.assertTrue("Nepodarilo a vlozit", inserted);
            }
        }
        insertedValues.sort((o1, o2) -> o1 - o2);
        ArrayList<Integer> resultList = new ArrayList<>();
        AvlTree.traverseTree(testAvlTreeAscendant_, resultList);
        for (int i = 0; i < insertedValues.size(); ++i) {
            Assert.assertEquals(insertedValues.get(i), resultList.get(i));
        }
        System.out.println("Result list size: " + resultList.size());
        for (Integer num: insertedValues) {
            //System.out.println("mazem: " + num);
            boolean deleted = testAvlTreeAscendant_.remove(num);
            Assert.assertTrue("Nepodarilo a vymazat", deleted);
        }
    }

    @Test
    public void testInOrderIterator() {
        Random generator = new Random();
        ArrayList<Integer> generatedValues = new ArrayList<>();
        IntStream stream = generator.ints(10000);
        stream.forEach(value -> generatedValues.add(value));

        generatedValues.forEach(value -> testAvlTreeAscendant_.insert(value));
        generatedValues.forEach(value -> {
            Assert.assertNotNull("Prvok nebol najdeny", testAvlTreeAscendant_.findData(value));
        });

        List<List<Integer>> resultList = new ArrayList<>();
        AvlTree.traverseTreeLevelOrder(testAvlTreeAscendant_, resultList);
        int levelNum = 1;
        for (List<Integer> level : resultList) {
            System.out.println(levelNum + " " + level);
            ++levelNum;
        }
    }

    @Test
    public void testRootDelete() {
        List<Integer> insertedValues = new LinkedList<>();
        Random generator = new Random();
        int iterations = 10000;

        for (int i = 0; i < iterations; i++) {
            int generated = generator.nextInt(iterations * 10);
            boolean found = false;
            for (Integer num: insertedValues) {
                if (num == generated) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                //System.out.println("generated: " + generated);
                insertedValues.add(generated);
                boolean inserted = testAvlTreeAscendant_.insert(generated);
                Assert.assertTrue("Nepodarilo a vlozit", inserted);
            }
        }

        while (insertedValues.size() > iterations / 2) {
            int position = Math.abs(generator.nextInt(iterations / 2));
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > iterations / 2) {
            int position = 1;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > iterations / 3) {
            int position = 2;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > iterations / 4) {
            int position = 3;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > iterations / 5) {
            int position = 4;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > iterations / 6) {
            int position = insertedValues.size() - 1;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > iterations / 7) {
            int position = insertedValues.size() - 2;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        while (insertedValues.size() > 0) {
            int position = 0;
            Integer dataAtPosition = insertedValues.remove(position);
            boolean removed =  testAvlTreeAscendant_.remove(dataAtPosition);
            Assert.assertTrue("nepodarilo sa vymazat prvok", removed);
        }
        Assert.assertEquals(0, testAvlTreeAscendant_.getSize());
    }

    private <T> void  printLevelOrder(AvlTree<T> tree) {
        List<List<T>> levels = new ArrayList<>();
        AvlTree.<T>traverseTreeLevelOrder(tree, levels);
        int levelNum = 1;
        for (List<T> level: levels) {
            System.out.println(levelNum++ + ": " + level);
        }
    }

}
