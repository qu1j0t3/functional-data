/*
    This file is part of Functional Data, experiments in Java
    Copyright (C) 2014 Toby Thain, toby@telegraphics.com.au

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package test.java.au.com.telegraphics.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Function;

import junit.framework.TestCase;

import main.java.au.com.telegraphics.data.*;

public class TestTree extends TestCase {

   protected static ArrayList<Integer> range(int from, int to) {
      ArrayList<Integer> list = new ArrayList<Integer>(to-from+1);
      for(int i = from; i <= to; ++i) {
         list.add(i);
      }
      return list;
   }

   protected static ArrayList<Integer> oneUpTo(int n) {
      return range(1, n);
   }
   
   protected static List<Integer> randomPerm(int n) {
      Random rnd = new Random();
      ArrayList<Integer> seq = oneUpTo(n);
      List<Integer> list = new LinkedList<Integer>();
      for(int j = n; j > 0; --j) {
         int idx = rnd.nextInt(j);
         list.add(seq.get(idx));
         seq.set(idx, seq.get(j-1));
      }
      return list;
   }
   
   protected static OrderedBinaryTree<Integer> randomTree(int n) {
      OrderedBinaryTree<Integer> t = OrderedBinaryTree.empty();

      for(Integer i : randomPerm(n)) {
         t = t.insert(i);
      }
      return t;
   }

   public void testEmptyTree() {
      OrderedBinaryTree<Integer> t = OrderedBinaryTree.empty();

      assertEquals(0, t.size());
      assertEquals(0, t.depth());
      assertTrue(t.isEmpty());
      assertTrue(t.min().isEmpty());
      assertTrue(t.max().isEmpty());
      assertEquals(new LinkedList<Integer>(), t.inOrder());
   }
   
   public void testOneElement() {
      OrderedBinaryTree<Integer> t = OrderedBinaryTree.<Integer>empty().insert(7);

      assertEquals(1, t.size());
      assertFalse(t.isEmpty());
      assertTrue(t.min().exists(7));
      assertTrue(t.max().exists(7));
      assertTrue(t.contains(7));
      assertFalse(t.contains(77));

      LinkedList<Integer> list = new LinkedList<Integer>();
      list.add(7);
      assertEquals(list, t.inOrder());
   }
   
   public void testTwoElements() {
      OrderedBinaryTree<Integer> t = OrderedBinaryTree.<Integer>empty().insert(7).insert(9);

      assertEquals(2, t.size());
      assertFalse(t.isEmpty());
      assertTrue(t.min().exists(7));
      assertTrue(t.max().exists(9));
      assertTrue(t.contains(7));
      assertTrue(t.contains(9));

      LinkedList<Integer> list = new LinkedList<Integer>();
      list.add(7);
      list.add(9);
      assertEquals(list, t.inOrder());
   }
   
   public void testManyElements() {
      final int COUNT = 100;
      OrderedBinaryTree<Integer> t = randomTree(COUNT);

      assertEquals(COUNT, t.size());
      assertFalse(t.isEmpty());
      assertTrue(t.min().exists(1));
      assertTrue(t.max().exists(COUNT));
      assertTrue(t.contains(42));

      assertEquals(oneUpTo(COUNT), t.inOrder());
   }
   
   public void testRemoveMin() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      Option<OrderedBinaryTree<Integer>.TreeAndValue> u = t.removeMin();
      
      OrderedBinaryTree<Integer> newTree = u.getOrElse(null).l;
      int removedValue = u.getOrElse(null).r;

      assertEquals(9, newTree.size());
      assertEquals(1, removedValue);
      assertFalse(newTree.contains(1));
      assertTrue(newTree.min().exists(2));
      assertTrue(newTree.max().exists(10));
   }
   
   public void testRemoveMax() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      Option<OrderedBinaryTree<Integer>.TreeAndValue> u = t.removeMax();
      
      OrderedBinaryTree<Integer> newTree = u.getOrElse(null).l;
      int removedValue = u.getOrElse(null).r;

      assertEquals(9, newTree.size());
      assertEquals(10, removedValue);
      assertFalse(newTree.contains(10));
      assertTrue(newTree.min().exists(1));
      assertTrue(newTree.max().exists(9));
   }
   
   public void testRemove() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      OrderedBinaryTree<Integer> newTree = t.remove(5).getOrElse(null);
      
      assertEquals(9, newTree.size());
      assertFalse(newTree.contains(5));
   }
   
   public void testRemoveAll() {
      OrderedBinaryTree<Integer> t = randomTree(10);
      
      for(Integer i : randomPerm(10)) {
         t = t.remove(i).getOrElse(null); // assumes element was removed
      }

      assertTrue(t.isEmpty());
   }
   
   public void testRemoveHalf() {
      OrderedBinaryTree<Integer> t = randomTree(100);
      
      for(Integer i : randomPerm(100)) {
         t = t.remove(i*2).getOrElse(t); // removes element or has no effect
      }

      assertEquals(50, t.size());
      assertTrue(t.min().exists(1));
      assertFalse(t.contains(2));
      assertTrue(t.max().exists(99));
   }
   
   public void testRemoveAbsentValue() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      Option<OrderedBinaryTree<Integer>> optTree = t.remove(50);
      
      assertTrue(optTree.isEmpty());
   }
   
   public void testInOrder() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      assertEquals(oneUpTo(10), t.inOrder());
   }
   
   public void testBetween() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      assertEquals(range(3, 7), t.between(3, 7));
   }
   
   protected static Function<Integer,Integer> addOne = new Function<Integer,Integer>() {
      public Integer apply(Integer x) { return x+1; }
   };
   
   public void testMap() {
      OrderedBinaryTree<Integer> t = randomTree(10);

      assertEquals(range(2, 11), t.map(addOne).inOrder());
   }
}
