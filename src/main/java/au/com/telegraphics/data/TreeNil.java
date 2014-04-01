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

package main.java.au.com.telegraphics.data;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;

public class TreeNil<T extends Comparable<T>> extends OrderedBinaryTree<T> {

   @Override
   public TreeNode<T> insert(T v) {
      return tree(new TreeNil<T>(), v, new TreeNil<T>());
   }

   @Override
   public Option<OrderedBinaryTree<T>> remove(T v) {
      return Option.none();
   }
   
   @Override
   public Option<T> min() {
      return Option.none();
   }
   
   @Override
   public Option<T> max() {
      return Option.none();
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public Option<TreeAndValue> removeMax() {
      return Option.none();
   }

   @Override
   public Option<TreeAndValue> removeMin() {
      return Option.none();
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public int depth() {
      return 0;
   }

   @Override
   public List<T> inOrder() {
      return new LinkedList<T>();
   }

   @Override
   public List<T> between(T min, T max) {
      return new LinkedList<T>();
   }

   @Override
   protected String toString(int depth) {
      return toString();
   }

   @Override
   public String toString() {
      return "()";
   }

   @Override
   public boolean contains(T v) {
      return false;
   }

   @Override
   public <U extends Comparable<U>> OrderedBinaryTree<U> map(Function<T,U> f) {
      return empty();
   }
}
