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

public abstract class OrderedBinaryTree<T extends Comparable<T>> {
	
	public static <U extends Comparable<U>> OrderedBinaryTree<U> empty() {
		return new TreeNil<U>();
	}

	public class TreeAndValue extends Pair<OrderedBinaryTree<T>,T> {
		public TreeAndValue(OrderedBinaryTree<T> t, T u) {
			super(t, u);
		}
	}
	
	abstract public TreeNode<T> insert(T v);

	/**
	 * @return Some if an element was removed from the tree; otherwise None
	 */
	abstract public Option<OrderedBinaryTree<T>> remove(T v);
	abstract public Option<TreeAndValue> removeMax();
	abstract public Option<TreeAndValue> removeMin();

	abstract public Option<T> max();
	abstract public Option<T> min();
	abstract public boolean isEmpty();
	abstract public int size();
	abstract public int depth();
	abstract public List<T> inOrder();
	abstract public boolean contains(T v);
	
	abstract protected String toString(int depth);
	
	protected TreeNode<T> tree(OrderedBinaryTree<T> l, T v, OrderedBinaryTree<T> r) {
		return new TreeNode<T>(l, v, r);
	}
}
