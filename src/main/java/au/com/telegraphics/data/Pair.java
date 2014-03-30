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

public class Pair<T,U> {
   public final T l;
   public final U r;

   public Pair(T t, U u) {
      this.l = t;
      this.r = u;
   }

    @Override
    public String toString() {
        return "(" + l + "," + r + ")";
    }

    @Override
    public boolean equals(Object that) {
        if(!(that instanceof Pair))
            return false;
        Pair p = (Pair)that;
        // won't handle nulls
        return l.equals(p.l) && r.equals(p.r);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31*result + l.hashCode();
        return 31*result + r.hashCode();
    }
}
