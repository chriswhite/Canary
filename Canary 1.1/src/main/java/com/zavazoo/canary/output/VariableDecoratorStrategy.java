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

import java.util.Collection;
import java.util.Map;

/**
 * Strategically creates the optimal variable decorator according to the type of
 * the variable.
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public abstract class VariableDecoratorStrategy {

	/**
	 * Strategically creates the optimal variable decorator according to the
	 * type of the specified variable using the specified maximum length of the
	 * representation of the variable.
	 * 
	 * @param variable
	 *            the variable.
	 * @param maximum
	 *            the maximum length.
	 * @return the decorator.
	 */
	public static VariableDecorator createDecoratorForVariableType(
			Object variable, int maximum) {

		VariableDecorator decorator = null;

		if (variable == null) {

			decorator = new SimpleVariableDecorator();

		} else if (variable instanceof Object[]) {

			decorator = new ArrayVariableDecorator();

		} else if (variable instanceof byte[]) {

			decorator = new ByteArrayVariableDecorator();

		} else if (variable instanceof short[]) {

			decorator = new ShortIntegerArrayVariableDecorator();

		} else if (variable instanceof int[]) {

			decorator = new IntegerArrayVariableDecorator();

		} else if (variable instanceof long[]) {

			decorator = new LongIntegerArrayVariableDecorator();

		} else if (variable instanceof float[]) {

			decorator = new FloatArrayVariableDecorator();

		} else if (variable instanceof double[]) {

			decorator = new DoubleIntegerArrayVariableDecorator();

		} else if (variable instanceof boolean[]) {

			decorator = new BooleanArrayVariableDecorator();

		} else if (variable instanceof char[]) {

			decorator = new CharacterArrayVariableDecorator();

		} else if (variable instanceof Collection) {

			decorator = new CollectionVariableDecorator();

		} else if (variable instanceof Map) {

			decorator = new MapVariableDecorator();

		} else {

			decorator = new SimpleVariableDecorator();

		}

		decorator.setVariable(variable);
		decorator.setMaximum(maximum);

		return decorator;

	}

}