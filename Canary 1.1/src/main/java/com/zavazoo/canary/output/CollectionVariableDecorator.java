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
import java.util.Iterator;

/**
 * Variable decorator that represents a variable that is a collection using the
 * following notation: (element1, element2, element3)
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public class CollectionVariableDecorator extends VariableDecorator {

	/**
	 * Represents the variable that is a collection.
	 * 
	 * @return the representation.
	 */
	public String representVariable() {

		Collection<?> collection = (Collection<?>) getVariable();
		int maximum = getMaximum();

		Iterator<?> collectionIterator = collection.iterator();

		StringBuilder representation = new StringBuilder();
		representation.append('(');

		while (collectionIterator.hasNext()) {

			Object element = collectionIterator.next();

			VariableDecorator elementDecorator = VariableDecoratorStrategy
					.createDecoratorForVariableType(element, maximum);

			String elementRepresentation = elementDecorator.representVariable();

			representation.append(elementRepresentation);

			if (collectionIterator.hasNext()) {

				representation.append(", ");

			}

			if (representation.length() > maximum) {

				return representation.toString();

			}

		}

		representation.append(')');

		return representation.toString();

	}

}