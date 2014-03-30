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

import java.util.List;

import com.google.common.base.Function;

public class TreeNode<T extends Comparable<T>> extends OrderedBinaryTree<T> {
   public final OrderedBinaryTree<T> left;
   public final OrderedBinaryTree<T> right;
   public final T value;
   
   public TreeNode(OrderedBinaryTree<T> l, T v, OrderedBinaryTree<T> r) {
      this.left  = l;
      this.value = v;
      this.right = r;
   }

   @Override
   public TreeNode<T> insert(T v) {
      if(v.compareTo(value) <= 0) {
         return tree(left.insert(v), value, right);
      } else {
         return tree(left, value, right.insert(v));
      }
   }

   @Override
   public boolean contains(T v) {
      int comp = v.compareTo(value);
      if(comp < 0) {
         return left.contains(v);
      } else if(comp > 0){
         return right.contains(v);
      } else {
         return true;
      }
   }
   
   protected Function<OrderedBinaryTree<T>,OrderedBinaryTree<T>> replaceLeft =
      new Function<OrderedBinaryTree<T>,OrderedBinaryTree<T>>() {
         public OrderedBinaryTree<T> apply(OrderedBinaryTree<T> t) {
            return tree(t, value, right);
         }
      };
      
   protected Function<OrderedBinaryTree<T>,OrderedBinaryTree<T>> replaceRight =
      new Function<OrderedBinaryTree<T>,OrderedBinaryTree<T>>() {
         public OrderedBinaryTree<T> apply(OrderedBinaryTree<T> t) {
            return tree(left, value, t);
         }
      };

   @Override
   public Option<OrderedBinaryTree<T>> remove(T v) {
      int comp = v.compareTo(value);
      if(comp < 0) {
         return left.remove(v).map(replaceLeft).orElse(Option.<OrderedBinaryTree<T>>none());
      } else if(comp > 0){
         return right.remove(v).map(replaceRight).orElse(Option.<OrderedBinaryTree<T>>none());
      } else {
         // this is the node to remove
         if(right.isEmpty()) {
            return Option.some(left);
         } else {
            return left.removeMax().map(
                  new Function<TreeAndValue,OrderedBinaryTree<T>>() {
                     public OrderedBinaryTree<T> apply(TreeAndValue tn) {
                        return tree(tn.l, tn.r, right);
                     }
                  } ).orElse(Option.some(right));
         }
      }
   }
   
   @Override
   public Option<T> min() {
      return left.min().orElse(Option.some(this.value));
   }
   
   @Override
   public Option<T> max() {
      return right.max().orElse(Option.some(this.value));
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public int size() {
      return left.size() + 1 + right.size();
   }

   @Override
   public int depth() {
      return Math.max(left.size(), right.size()) + 1;
   }

   @Override
   public Option<TreeAndValue> removeMax() {
      if(right.isEmpty()) {
         return Option.some(new TreeAndValue(left, value));
      } else {
         return right.removeMax().map(
               new Function<TreeAndValue, TreeAndValue>() {
                  public TreeAndValue apply(TreeAndValue tn) {
                     return new TreeAndValue(tree(left, value, tn.l), tn.r);
                  }
               } );
      }
   }

   @Override
   public Option<TreeAndValue> removeMin() {
      if(left.isEmpty()) {
         return Option.some(new TreeAndValue(right, value));
      } else {
         return left.removeMin().map(
               new Function<TreeAndValue, TreeAndValue>() {
                  public TreeAndValue apply(TreeAndValue tn) {
                     return new TreeAndValue(tree(tn.l, value, right), tn.r);
                  }
               } );
      }
   }

   @Override
   public List<T> inOrder() {
      List<T> elements = left.inOrder();
      elements.add(value);
      elements.addAll(right.inOrder());
      return elements;
   }

   @Override
   protected String toString(int depth) {
      return depth > 0 ? "<" + left.toString(depth-1) + ":"
                           + value + ":"
                           + right.toString(depth-1) + ">"
                     : "...";
   }
   
   @Override
   public String toString() {
      return toString(3);
   }
}
