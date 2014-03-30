package main.scala.au.com.telegraphics.data

// Copyright (C) 2013-2014 Toby Thain, toby@telegraphics.com.au

import util.Random

object Median {

  // This isn't useful for anything at the moment. Ignore.

  def randomPartition[T <% Ordered[T]](s:IndexedSeq[T]): (IndexedSeq[T],IndexedSeq[T]) = {
    val k = Random.nextInt(s.size)
    val pivot = s(k)
    (s.filter(pivot >), s.filter(pivot <=))  // two O(n) passes
  }

  def smallestN[T <% Ordered[T]](k:Int, s:IndexedSeq[T]):IndexedSeq[T] = {
    val (a,b) = randomPartition(s)
    if(a.size == k)
      a
    else if(a.size < k)
      smallestN(k - a.size, b)
    else
      smallestN(k, a)
  }
}