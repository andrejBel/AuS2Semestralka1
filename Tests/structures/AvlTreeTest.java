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
        int iteration = 10;
        double totalTime = 0;
        for (int j = 0; j < iteration; j++) {
            testAvlTreeAscendant_.clear();
            long start = System.nanoTime();
            for (int i = 10000000; i > 0; i--) {
                Assert.assertTrue(testAvlTreeAscendant_.insert(i));
                //Assert.assertTrue("Nie je spnena podmienka AVL stromu" ,testAvlTreeAscendant_.checkAvlTreeConditions());
            }
            long end = (System.nanoTime() - start) / 1000000;
            totalTime += end;
        }
        System.out.println("Average time[miliseconds] for insert is: " + (totalTime / (iteration)));
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
        AvlTree.TraverseTree(testAvlTreeAscendant_, resultList);
        for (int i = 0; i < listOfValues.size(); ++i ) {
            Assert.assertEquals(listOfValues.get(i), resultList.get(i));
        }
    }

    @Test
    public void testRandomInsertion() {
        for (int k = 0; k < 1; k++) {
            testAvlTreeAscendant_.clear();
            Map<Integer, Integer> insertedTree = new TreeMap<>((o1, o2) -> o1 - o2);
            Random generator = new Random();


            int iterations = 1000000;

            for (int i = 0; i < iterations; i++) {
                int generated = generator.nextInt(iterations * 2);
                boolean found = insertedTree.containsKey(generated);
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


            ArrayList<Integer> resultList = new ArrayList<>(insertedTree.size());
            AvlTree.TraverseTree(testAvlTreeAscendant_, resultList);
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
    public void testRemoveByHand() {
        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(9);

        testAvlTreeAscendant_.remove(10);


        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(11);
        testAvlTreeAscendant_.insert(9);

        Assert.assertTrue(testAvlTreeAscendant_.remove(10)); ;
        Assert.assertNull(testAvlTreeAscendant_.findData(10));
        printLevelOrder(testAvlTreeAscendant_);
        testAvlTreeAscendant_.clear();

        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(11);
        testAvlTreeAscendant_.insert(9);
        Assert.assertTrue(testAvlTreeAscendant_.remove(11));
        Assert.assertNull(testAvlTreeAscendant_.findData(11));
        printLevelOrder(testAvlTreeAscendant_);
        Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());
        testAvlTreeAscendant_.clear();

        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(11);
        testAvlTreeAscendant_.insert(9);
        Assert.assertTrue(testAvlTreeAscendant_.remove(9));
        Assert.assertNull(testAvlTreeAscendant_.findData(9));
        printLevelOrder(testAvlTreeAscendant_);
        Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());
        testAvlTreeAscendant_.clear();


        // left right
        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(12);
        testAvlTreeAscendant_.insert(8);
        testAvlTreeAscendant_.insert(9);
        Assert.assertTrue(testAvlTreeAscendant_.remove(12));
        Assert.assertNull(testAvlTreeAscendant_.findData(12));
        printLevelOrder(testAvlTreeAscendant_);
        Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());
        testAvlTreeAscendant_.clear();

        // right
        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(12);
        testAvlTreeAscendant_.insert(8);
        testAvlTreeAscendant_.insert(7);
        Assert.assertTrue(testAvlTreeAscendant_.remove(12));
        Assert.assertNull(testAvlTreeAscendant_.findData(12));
        printLevelOrder(testAvlTreeAscendant_);
        Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());
        testAvlTreeAscendant_.clear();

        // right left
        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(12);
        testAvlTreeAscendant_.insert(9);
        testAvlTreeAscendant_.insert(11);
        Assert.assertTrue(testAvlTreeAscendant_.remove(9));
        Assert.assertNull(testAvlTreeAscendant_.findData(9));
        printLevelOrder(testAvlTreeAscendant_);
        Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());
        testAvlTreeAscendant_.clear();

        // left
        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(12);
        testAvlTreeAscendant_.insert(9);
        testAvlTreeAscendant_.insert(14);
        Assert.assertTrue(testAvlTreeAscendant_.remove(9));
        Assert.assertNull(testAvlTreeAscendant_.findData(9));
        printLevelOrder(testAvlTreeAscendant_);
        Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());
        testAvlTreeAscendant_.clear();


        /*
        testAvlTreeAscendant_.insert(10);
        testAvlTreeAscendant_.insert(11);
        Assert.assertTrue(testAvlTreeAscendant_.remove(10)); ;
        Assert.assertNull(testAvlTreeAscendant_.findData(10));
        printLevelOrder(testAvlTreeAscendant_);

        ArrayList<Integer> currentCaseInsert = null;
        ArrayList<Integer> currentCaseRemove = null;

        ArrayList<Integer> case1Insert = new ArrayList<>(Arrays.asList(10));
        ArrayList<Integer> case1Remove = new ArrayList<>(Arrays.asList(10));
        currentCaseInsert = case1Insert;
        currentCaseRemove = case1Remove;


        currentCaseInsert.forEach(integer -> {
            Assert.assertTrue(testAvlTreeAscendant_.insert(integer));
            Assert.assertNotNull(testAvlTreeAscendant_.findData(integer));
        });
        currentCaseRemove.forEach(integer -> {
            Assert.assertTrue(testAvlTreeAscendant_.remove(integer)); ;
            Assert.assertNull(testAvlTreeAscendant_.findData(integer));
        });

        */

    }

    @Test
    public void testRandomRemove() {
        Map<Integer, Integer> insertedTreeMap = new TreeMap<>();

        Random generator = new Random();
        int iterations = 100000;

        for (int i = 0; i < iterations; i++) {
            int generated = generator.nextInt(iterations * 3);
            boolean found = insertedTreeMap.containsKey(generated);
            if (found) {
                continue;
            }
            if (!found) {
                //System.out.println("generated: " + generated);
                //insertedValues.add(generated);
                insertedTreeMap.put(generated, generated);
                boolean inserted = testAvlTreeAscendant_.insert(generated);
                Assert.assertTrue("Nepodarilo a vlozit", inserted);
                Assert.assertNotNull("Nepodarilo a vlozit", testAvlTreeAscendant_.findData(generated));
            }
        }
        //insertedValues.sort((o1, o2) -> o1 - o2);

        ArrayList<Integer> resultList = new ArrayList<>(insertedTreeMap.size());
        testAvlTreeAscendant_.resetCounter();
        while (insertedTreeMap.size() > 0) {
            int indexToRemove = generator.nextInt(insertedTreeMap.size());
            Object[] keys = insertedTreeMap.keySet().toArray();
            Integer numberToRemove = (Integer) keys[indexToRemove];
            insertedTreeMap.remove(numberToRemove);

            Assert.assertTrue(testAvlTreeAscendant_.remove(numberToRemove));
            Assert.assertNull(testAvlTreeAscendant_.findData(numberToRemove));
            Assert.assertTrue(testAvlTreeAscendant_.checkAvlTreeConditions());

            resultList.clear();
            AvlTree.TraverseTree(testAvlTreeAscendant_, resultList);

            Assert.assertEquals(resultList.size(), insertedTreeMap.size());
            Object[] keyAfterRemove = insertedTreeMap.keySet().toArray();
            int index = 0;
            for (Object num: keyAfterRemove) {
                Assert.assertEquals( (Integer)num , resultList.get(index++));
            }
            /*
                if (indexToRemove % 317 == 1 && insertedTreeMap.size() < iterations) {
                int toInsert = -numberToRemove - 1;
                if (toInsert < 0)
                {
                    insertedTreeMap.put(toInsert, toInsert);
                    boolean inserted = testAvlTreeAscendant_.insert(toInsert);
                    Assert.assertTrue("Nepodarilo a vlozit", inserted);
                    Assert.assertNotNull("Nepodarilo a vlozit", testAvlTreeAscendant_.findData(toInsert));
                }
            }
            */

        }

        testAvlTreeAscendant_.printCounter();
    }

    @Test
    public void testRootDelete() {
        List<Integer> insertedValues = new LinkedList<>();
        Random generator = new Random();
        int iterations = 100000;

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
        AvlTree.<T>TraverseTreeLevelOrder(tree, levels);
        int levelNum = 1;
        for (List<T> level: levels) {
            System.out.println(levelNum++ + ": " + level);
        }
    }

}
