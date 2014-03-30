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

// A 2-3 tree is always balanced (all leaves are at the same depth),
// so operations like contains, min, max, add are O(log n).
// It is also ordered, immutable and persistent.

sealed abstract class TwoThreeTree[A <% Ordered[A]] {
  type MaybePseudo = Either[PseudoTwo[A],TwoThreeTree[A]]
  type MaybeHole   = Either[TwoThreeTree[A],TwoThreeTree[A]]

  final def add(value: A): TwoThreeTree[A] =
    pseudoAdd(value).fold(_.asTwo,   // If "kicked up", promote pseudo node to root; tree depth increases by 1
                          identity)  // otherwise, use new tree value

  final def remove(value: A): Option[TwoThreeTree[A]] =
    pseudoRemove(value).map(
      _.fold(identity,      // hole bubbles off the top, leaving new root
             identity  // tree has already been resolved
      ))

  final def addList(as: List[A]): TwoThreeTree[A] =
    as.foldLeft(this)((t, a) => t.add(a))

  final protected def promote(e: MaybePseudo,
                              f: PseudoTwo[A] => MaybePseudo,
                              g: TwoThreeTree[A] => TwoThreeTree[A]): MaybePseudo =
    e.fold(f, t => Right(g(t)))

  def promoteHole(e:Option[MaybeHole],
                  f:TwoThreeTree[A]=>MaybeHole,
                  g:TwoThreeTree[A]=>TwoThreeTree[A]): Option[MaybeHole] =
    e.map( _.fold( f,  // may resolve tree, or leave hole for caller to deal with
                   tree => Right(g(tree)) ) ) // resolved the tree

  def pseudoAdd(value: A): MaybePseudo  // FIXME make this inaccessible outside this file
  def pseudoRemove(value: A): Option[MaybeHole]  // FIXME make this inaccessible outside this file

  def depth:Int
  def size:Int
  def contains(value: A): Boolean
  def min: Option[A]
  def max: Option[A]
  def inOrder: List[A]
}

case class PseudoTwo[A <% Ordered[A]](w:A, l:TwoThreeTree[A], r:TwoThreeTree[A]) {
  def asTwo = Two(w, l, r)
}

case class Hole[A <% Ordered[A]](t:TwoThreeTree[A]) {
}

// A leaf has no value
case class Leaf[A <% Ordered[A]]() extends TwoThreeTree[A] {
  def pseudoAdd(value:A) = Left(PseudoTwo(value, Leaf(), Leaf()))
  def pseudoRemove(value:A) = None
  def depth = 0
  def size = 0
  def contains(value:A) = false
  def min = None
  def max = None
  def inOrder = Nil
}

// A 2-node has one value, and two subtrees,
// conforming to values <= X, and values >= X respectively
case class Two[A <% Ordered[A]](x:A, l:TwoThreeTree[A], r:TwoThreeTree[A])
        extends TwoThreeTree[A] {
  def pseudoAdd(value:A) =
    if(value <= x)
      promote(l.pseudoAdd(value), // try adding to left subtree
              p => Right(Three(p.w, x, p.l, p.r, r)), // if result is "kicked up", this 2-node becomes a 3-node
              Two(x, _, r))  // otherwise, just produce a new 2-node incorporating new subtree
    else
      promote(r.pseudoAdd(value), // try adding to right subtree
              p => Right(Three(x, p.w, l, p.l, p.r)),
              Two(x, l, _))  // update with new subtree

  def pseudoRemove(value: A): Option[MaybeHole] =
    this match {
      // terminal cases
      case Two(`value`, Leaf(), Leaf()) => Some(Left(Leaf()))  // hit X
      case Two(_,       Leaf(), Leaf()) => None   // miss
      // populated cases
      case Two(_, _, r @ Two(_, _, _)) if(value < x) =>
        promoteHole(l.pseudoRemove(value),
                    hole => Left(Three(x, r.x, hole, r.l, r.r)), // deletion in the subtree produced a hole
                    Two(x, _, r)) // deletion in subtree resolved to a tree of same height (e.g. 3-node shrank to a 2-node)
      case Two(_, l @ Two(_, _, _), _) if(value >= x) =>
        promoteHole(r.pseudoRemove(value),
                    hole => Left(Three(l.x, x, l.l, l.r, hole)),
                    Two(x, l, _))
      case Two(_, _, r @ Three(_, _, _, _, _)) if(value < x) =>
        promoteHole(l.pseudoRemove(value),
                    hole => Right(Three(x, r.x, hole, r.l, r.r)),
                    Two(x, _, r)) // update with the resolved subtree
      case Two(_, l @ Three(_, _, _, _, _), _) if(value >= x) =>
        promoteHole(r.pseudoRemove(value),
                    hole => Right(Three(l.x, x, l.l, l.r, hole)),
                    Two(x, l, _)) // update with resolved subtree
      case Two(_, Leaf(), _) | Two(_, _, Leaf()) => sys.error("bug")  // cases where there is just 1 Leaf subtree are not valid
      case _ => None
    }
  
  def depth = 1 + l.depth
  def size = 1 + l.size + r.size
  def contains(value:A) =
    if(value < x) l.contains(value)
    else value == x || r.contains(value)
  def min = l.min.orElse(Some(x))
  def max = r.max.orElse(Some(x))
  def inOrder = l.inOrder ++ (x :: r.inOrder)  // FIXME this needs to be a proper fold, without ++
}

// A 3-node has two values, and three subtrees, conforming to values <= X,
// X <= value <= Y, and values >= Y respectively.
case class Three[A <% Ordered[A]](x:A, y:A, l:TwoThreeTree[A], m:TwoThreeTree[A], r:TwoThreeTree[A])
        extends TwoThreeTree[A] {
  def pseudoAdd(value:A) =
    if(value <= x)
      promote(l.pseudoAdd(value),    // try adding to left subtree
              p => Left(PseudoTwo(x, p.asTwo, Two(y, m, r))), // if kicked up, split into two 2-nodes, new node is left one
              Three(x, y, _, m, r))  // otherwise, just produce new 3-node incorporating new subtree
    else if(value <= y)
      promote(m.pseudoAdd(value),    // try adding to middle subtree
              p => Left(PseudoTwo(p.w, Two(x, l, p.l), Two(y, p.r, r))), // split into two 2-nodes, new node split between left and right
              Three(x, y, l, _, r))  // update with new subtree
    else
      promote(r.pseudoAdd(value),    // try adding to right subtree
              p => Left(PseudoTwo(y, Two(x, l, m), p.asTwo)), // split into two 2-nodes, new node becoming right one
              Three(x, y, l, m, _))  // update with new subtree

  def pseudoRemove(value: A): Option[MaybeHole] =
    this match {
      // terminal cases
      case Three(`value`, y, Leaf(), Leaf(), Leaf()) => Some(Right( Two(y, Leaf(), Leaf()) ))
      case Three(x, `value`, Leaf(), Leaf(), Leaf()) => Some(Right( Two(x, Leaf(), Leaf()) ))
      case Three(_, _,       Leaf(), Leaf(), Leaf()) => None   // miss
      case Three(_, _, _, m @ Two(_, _, _), _) if value < x =>
        promoteHole(l.pseudoRemove(value),
                    hole => Right(Two(y, Three(x, m.x, hole, m.l, m.r), r)), // hole is resolved
                    Three(x, y, _, m, r))
      case Three(_, _, _, m @ Two(_, _, _), _) if value >= y =>
        promoteHole(r.pseudoRemove(value),
                    hole => Right(Two(y, Three(x, m.x, hole, m.l, m.r), r)), // hole is resolved
                    Three(x, y, _, m, r))
      case Three(_, _, l @ Two(_, _, _), _, _) if value >= x && value < y =>
        promoteHole(m.pseudoRemove(value),
                    hole => Right(Two(y, Three(l.x, x, l.l, l.r, hole), r)), // hole is resolved
                    Three(x, y, l, _, r))
      case Three(_, _, _, _, r @ Two(_, _, _)) if value >= x && value < y =>
        promoteHole(m.pseudoRemove(value),
                    hole => Right(Two(x, l, Three(y, r.x, hole, r.l, r.r))), // hole is resolved
                    Three(x, y, l, _, r))
      //case _ => sys.error("bug")  // cases where there is just 1 Leaf subtree are not valid
    }

  def depth = 1 + l.depth
  def size = 2 + l.size + m.size + r.size
  def contains(value: A) =
    if(value < x) l.contains(value)
    else value == x || (if(value < y) m.contains(value)
                        else value == y || r.contains(value))
  
  def min = l.min.orElse(Some(x))
  def max = r.min.orElse(Some(y))
  def inOrder = l.inOrder ++ (x :: m.inOrder ++ (y :: r.inOrder))  // FIXME this needs to be a proper fold, without ++
}

object TwoThreeTree {
  import util.Random

  def empty[A <% Ordered[A]]: TwoThreeTree[A] = Leaf()  // a tree of A with no values

  def print[A <% Ordered[A]](t:TwoThreeTree[A]) {
    def node(depth:Int, n:TwoThreeTree[A]) {
      val indent = "      "*depth
      val d = depth+1
      n match {
        case Leaf() => ()
        case Two(x, l, r) =>
          node(d, l)
          println("%sx: %s".format(indent, x))
          node(d, r)
        case Three(x, y, l, m, r) =>
          node(d, l)
          println("%sx: %s".format(indent, x))
          node(d, m)
          println("%sy: %s".format(indent, y))
          node(d, r)
      }
    }
    node(0, t)
  }

  def randomTree(n:Int):TwoThreeTree[Int] =
    if(n == 0) empty
    else randomTree(n-1).add(Random.nextInt(1000))
}
