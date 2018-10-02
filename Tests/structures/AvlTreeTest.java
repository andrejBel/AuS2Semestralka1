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
        for (int i = 10000; i > 0; i--) {
            Assert.assertTrue(testAvlTreeAscendant_.insert(i));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
        }
        testAvlTreeAscendant_.printCounter();
    }

    @Test
    public void testAvlTreeLeftRotate() {
        for (int i = 0; i < 10000; i++) {
            Assert.assertTrue(testAvlTreeAscendant_.insert(i));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
        }
        testAvlTreeAscendant_.printCounter();
    }

    @Test
    public void testAvlTreeRightLeftRotate() {
        for (int i = 0; i < 10000; i += 3) {
            int first = i;
            int second = i + 2;
            int third = i + 1;
            Assert.assertTrue(testAvlTreeAscendant_.insert(first));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
            Assert.assertTrue(testAvlTreeAscendant_.insert(second));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
            Assert.assertTrue(testAvlTreeAscendant_.insert(third));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
        }
        testAvlTreeAscendant_.printCounter();
        printLevelOrder(testAvlTreeAscendant_);
    }

    @Test
    public void testAvlTreeLeftRightRotate() {
        for (int i = 0; i < 100000; i += 3) {
            int first = i + 2;
            int second = i;
            int third = i + 1;
            Assert.assertTrue(testAvlTreeAscendant_.insert(first));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
            Assert.assertTrue(testAvlTreeAscendant_.insert(second));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
            Assert.assertTrue(testAvlTreeAscendant_.insert(third));
            Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
        }
        testAvlTreeAscendant_.printCounter();
        printLevelOrder(testAvlTreeAscendant_);
    }


    @Test
    public void testAvlTreeInsertionByHand() {
        testAvlTreeAscendant_.insert(6);
        testAvlTreeAscendant_.insert(4);
        testAvlTreeAscendant_.insert(5);
        printLevelOrder(testAvlTreeAscendant_);
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
        for (int k = 0; k < 1; k++) {
            testAvlTreeAscendant_.clear();
            Map<Integer, Integer> insertedTree = new TreeMap<>((o1, o2) -> o1 - o2);
            ArrayList<Integer> insertedValues = new ArrayList<>();
            Random generator = new Random();


            int iterations = 10000000;

            for (int i = 0; i < iterations; i++) {
                int generated = generator.nextInt(iterations * 2);
                boolean found = insertedTree.get(generated) != null ? true : false;
                if (found) {
                    continue;
                }
                if (!found) {
                    //System.out.println("vkladam: " + generated);
                    insertedTree.put(generated, generated);
                    boolean inserted = testAvlTreeAscendant_.insert(generated);
                    Assert.assertTrue("Nepodarilo a vlozit", inserted);
                    Assert.assertNotNull(testAvlTreeAscendant_.findData(generated));
                    //Assert.assertTrue("Avl condition nedodrzana", testAvlTreeAscendant_.checkAvlTreeConditions());
                    //printLevelOrder(testAvlTreeAscendant_);

                }
            }
            //testAvlTreeAscendant_.printCounter();


            ArrayList<Integer> resultList = new ArrayList<>(insertedValues.size());
            AvlTree.traverseTree(testAvlTreeAscendant_, resultList);
            Set<Map.Entry<Integer, Integer>> entrySet = insertedTree.entrySet();
            int i = 0;
            for (Map.Entry<Integer,Integer> pair : entrySet) {
                int treeValue = resultList.get(i);
                Assert.assertEquals( pair.getKey().intValue(), treeValue);
                ++i;
            }

            System.out.println("Result list size: " + resultList.size());
            testAvlTreeAscendant_.printCounter();
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
