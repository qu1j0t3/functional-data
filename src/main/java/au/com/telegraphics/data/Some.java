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

import com.google.common.base.Function;

public final class Some<T> extends Option<T> {
	
	protected final T value;
	
	public Some(T v) {
		this.value = v;
	}

	@Override
	public T getOrElse(T defaultValue) {
		return value;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean exists(T value) {
		return this.value.equals(value);
	}

	@Override
	public Option<T> orElse(Option<T> opt) {
		return this;
	}

	@Override
	public <U> Option<U> map(Function<T, U> f) {
		return new Some<U>(f.apply(value));
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof Some && ((Some)that).value.equals(value);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString() {
		return "Some(" + value.toString() + ")";
	}
}
