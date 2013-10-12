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
 * Variable decorator that simply uses the representation resulting from the
 * toString() operation exposed by the variable.
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public class SimpleVariableDecorator extends VariableDecorator {

	/**
	 * Yields the result of a call to the toString() operation exposed by the
	 * variable notwithstanding a null variable that will be represented as
	 * 'null'.
	 * 
	 * @return the representation.
	 */
	public String representVariable() {

		Object variable = getVariable();

		if (variable == null) {

			return "null";

		}

		if (variable instanceof Character) {

			char character = (Character) variable;

			if (character == 0) {

				// yield null for the \u0000 character to avoid erroneous
				// representations in some environments
				return "null";

			}

		}

		return variable.toString();

	}

}