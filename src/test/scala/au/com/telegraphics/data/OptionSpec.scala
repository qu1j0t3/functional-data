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

package test.scala.au.com.telegraphics.data

import org.specs2.mutable._

import com.google.common.base.Function

import main.java.au.com.telegraphics.data.Option

class OptionSpec extends Specification {

  "A Some value" should {
    "contain a value" in {
      Option.some(1).isEmpty must beFalse
    }
    "be testable for wrapped value" in {
      Option.some(1).exists(1) must beTrue
    }
    "produce wrapped value" in {
      Option.some(7).getOrElse(99) must beEqualTo(7)
    }
    "be mappable" in {
      Option.some(6).map(
        new Function[Int,Int]() {
          override def apply(d:Int) = d*10
        } ).getOrElse(0) must beEqualTo(60)
    }
    "deliver itself via orElse" in {
      Option.some(3).orElse(Option.some(99)) must
              beEqualTo(Option.some(3))
    }
    "be equal if wrapped values are equal" in {
      Option.some(3) must beEqualTo(Option.some(3))
      Option.some("zig") must beEqualTo(Option.some("zig"))
      Option.some("bart") must not be equalTo(Option.some("lisa"))
    }
  }

  "A None value" should {
    "not contain a value" in {
      Option.none.isEmpty must beTrue
      Option.none[Int].exists(4) must beFalse
    }
    "map to None" in {
      Option.none[Int].map(
        new Function[Int,Int]() {
          override def apply(d:Int) = d*10
        } ).isEmpty must beTrue
    }
    "produce a default value" in {
      Option.none[Int].getOrElse(99) must beEqualTo(99)
    }
    "deliver the alternative via orElse" in {
      Option.none[Int].orElse(Option.some(99)) must
              beEqualTo(Option.some(99))
    }
    "be equal to any other None" in {
      Option.none must beEqualTo(Option.none)
      Option.none[Int] must beEqualTo(Option.none[Double])
    }
    "not be equal to a Some" in {
      Option.none must not be equalTo(Option.some(9))
    }
  }

}
