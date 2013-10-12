/*
 * Zavazoo Canary 1.1 - Java API for extended non-intrusive trace logging 
 * Copyright (C) 2011-2013 Chris White <chriswhitelondon@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zavazoo.canary.output;

/**
 * Defines a component that decorates the value of a variable in order to
 * produce a human-readable representation of the value.
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public abstract class VariableDecorator {

	/** The variable. */
	private Object variable;

	/** The maximum length of the representation of the variable. */
	private int maximum;

	/**
	 * Produces a human-readable representation of the value of the variable
	 * encapsulated by this decorator.
	 * 
	 * @return the representation.
	 */
	public abstract String representVariable();

	/**
	 * Gets the variable property.
	 * 
	 * @return the variable property.
	 */
	protected Object getVariable() {

		return variable;

	}

	/**
	 * Sets the variable property.
	 * 
	 * @param variable
	 *            the variable property.
	 */
	protected void setVariable(Object variable) {

		this.variable = variable;

	}

	/**
	 * Gets the maximum property.
	 * 
	 * @return the maximum property.
	 */
	protected int getMaximum() {

		return maximum;

	}

	/**
	 * Sets the maximum property.
	 * 
	 * @param maximum
	 *            the maximum property.
	 */
	protected void setMaximum(int maximum) {

		this.maximum = maximum;

	}

}