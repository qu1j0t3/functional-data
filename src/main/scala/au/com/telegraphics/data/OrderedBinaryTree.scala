package main.scala.au.com.telegraphics.data

// Copyright (C) 2013-2014 Toby Thain, toby@telegraphics.com.au
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

// tpolecat | if you import Ordering.Implicits._ you can replace
//            the view bound A <% Ordered[A] with a context bound A : Ordered
// tpolecat | A <% B means there's an implicit A => B
// tpolecat | A : B means there's an implicit B[A]
// tpolecat | A <% B[A] in this case, which means implicit A => B[A]
// tpolecat |  Foo a => (a -> b) is more or less the same as [A : Foo, B](a:A):B
// tpolecat | which is the same as [A,B](a:A)(implicit f: Foo[A]):B

sealed abstract class OrderedBinaryTree[T <% Ordered[T]] {
  def add(value:T):OrderedBinaryTree[T]
  def addUnique(value:T):Option[OrderedBinaryTree[T]]

  // insert a NON-EMPTY tree based on its value at the root
  // (given tree is not checked for ordering invariant)
  def addTree(tree:BinaryBranch[T]):OrderedBinaryTree[T] // FIXME should probably be accessible only in this file

  // remove produces Some(t) if a removal occurred, resulting in
  // a new tree value; otherwise None
  def remove(value:T):Option[OrderedBinaryTree[T]]

  def size:Int
  def depth:Int
  def inOrder:List[T]
  def range(lo:T, hi:T):List[T]
  def contains(value:T):Boolean
  def min:Option[T]
  def max:Option[T]
}

case class BinaryLeaf[T <% Ordered[T]]() extends OrderedBinaryTree[T] {
  def add(value:T) = BinaryBranch(value, BinaryLeaf(), BinaryLeaf())
  def addUnique(value:T) = Some(add(value))
  def addTree(tree:BinaryBranch[T]) = tree  // simply replaces this leaf
  def remove(value:T) = None
  def size = 0
  def depth = 0
  def inOrder = Nil
  def range(lo:T, hi:T) = Nil
  def contains(value:T) = false
  def min = None
  def max = None
}

case class BinaryBranch[T <% Ordered[T]](x:T,
                                         l:OrderedBinaryTree[T],
                                         r:OrderedBinaryTree[T])
  extends OrderedBinaryTree[T] {

  def add(value:T) =
    if(value <= x) BinaryBranch(x, l.add(value), r)
              else BinaryBranch(x, l, r.add(value))

  def addUnique(value:T) =
    if(value == x) None
    else if(value < x) l.addUnique(value).map(BinaryBranch(x, _, r))
                  else r.addUnique(value).map(BinaryBranch(x, l, _))

  def addTree(tree:BinaryBranch[T]) =
    if(tree.x <= x) BinaryBranch(x, l.addTree(tree), r)
               else BinaryBranch(x, l, r.addTree(tree))

  def remove(value:T) = {
    if(value == x)
      Some(
        this match {
          case BinaryBranch(_, BinaryLeaf(), BinaryLeaf()) => BinaryLeaf() // no descendants
          case BinaryBranch(_, BinaryLeaf(), right)        => right  // only has right subtree
          case BinaryBranch(_, left,         BinaryLeaf()) => left   // only has left subtree
          // if both subtrees exist, the choice of which to promote is pretty arbitrary.
          // try both, and take the more shallow result.
          case BinaryBranch(_, left @ BinaryBranch(_, _, _), right @ BinaryBranch(_, _, _)) =>
            val tryL = left.addTree(right)
            val tryR = right.addTree(left)
            if(tryL.depth < tryR.depth) tryL else tryR
        } )
    else if(value < x) l.remove(value).map(BinaryBranch(x, _, r))
                  else r.remove(value).map(BinaryBranch(x, l, _))
  }

  def size = 1 + l.size + r.size
  def depth = 1 + (l.depth max r.depth)
  def inOrder = l.inOrder ++ (x :: r.inOrder)
  def range(lo:T, hi:T) =
    (if(x >= lo) l.range(lo, hi)    else Nil) ++
    (if(lo <= x && x <= hi) List(x) else Nil) ++
    (if(x <= hi) r.range(lo, hi)    else Nil)

  def contains(value:T) =
    if(value < x) l.contains(value)
    else value == x || r.contains(value)

  def min = l.min.orElse(Some(x))
  def max = r.max.orElse(Some(x))
}

object OrderedBinaryTree {
  import util.Random
  
  def empty[T <% Ordered[T]]:OrderedBinaryTree[T] = BinaryLeaf()

  def print[T <% Ordered[T]](t:OrderedBinaryTree[T]) {
    def node(depth:Int, n:OrderedBinaryTree[T]) {
      val indent = "      "*depth
      val d = depth+1
      n match {
        case BinaryLeaf() => ()
        case BinaryBranch(x, l, r) =>
          node(depth+1, l)
          println(indent + x)
          node(depth+1, r)
      }
    }
    node(0, t)
  }

  def randomTree(n:Int):OrderedBinaryTree[Int] =
    if(n <= 0) empty
    else randomTree(n-1).add(Random.nextInt(1000))
  
  // Split list at midpoint (if sorted, this is the median).
  // Not defined for empty lists.
  def bisect[T](xs:List[T]):(List[T],T,List[T]) = {
    val (as,b::bs) = xs.splitAt(xs.size/2)
    (as,b,bs)
  }

  // Produce an optimally balanced tree from a list.
  def balanced[T <% Ordered[T]](xs:List[T]):OrderedBinaryTree[T] = {
    def balanceSorted: List[T] => OrderedBinaryTree[T] = {
      case Nil => BinaryLeaf()
      case nonEmpty =>
        val (as,b,bs) = bisect(nonEmpty)
        BinaryBranch(b, balanceSorted(as), balanceSorted(bs))
    }
    balanceSorted(xs.sorted)
  }
}