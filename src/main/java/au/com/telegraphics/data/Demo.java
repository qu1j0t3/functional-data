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

public class Demo {
   
   class MyPair extends Pair<Integer,Double> {
      public MyPair(Integer a, Double b) {
         super(a, b);
      }
   }
   
   class MyKey<T> extends Pair<String,T> {
      public MyKey(String a, T b) {
         super(a, b);
      }
   }

   public static void main(String[] args) {
      Pair<Integer,String> p = new Pair<Integer,String>(5, "a");
   }

}
