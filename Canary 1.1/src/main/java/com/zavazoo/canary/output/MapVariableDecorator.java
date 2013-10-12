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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Variable decorator that represents a variable that is a map using the
 * following notation: {entry1key => entry1value, entry2key => entry2value,
 * entry3key => entry3value}
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public class MapVariableDecorator extends VariableDecorator {

	/**
	 * Represents the variable that is a map.
	 * 
	 * @return the representation.
	 */
	public String representVariable() {

		Map<?, ?> map = (Map<?, ?>) getVariable();
		int maximum = getMaximum();

		Set<?> mapEntrySet = map.entrySet();

		Iterator<?> mapIterator = mapEntrySet.iterator();

		StringBuilder representation = new StringBuilder();
		representation.append('{');

		while (mapIterator.hasNext()) {

			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) mapIterator.next();

			Object entryKey = entry.getKey();
			Object entryValue = entry.getValue();

			VariableDecorator entryKeyDecorator = VariableDecoratorStrategy
					.createDecoratorForVariableType(entryKey, maximum);

			String entryKeyRepresentation = entryKeyDecorator
					.representVariable();

			VariableDecorator entryValueDecorator = VariableDecoratorStrategy
					.createDecoratorForVariableType(entryValue, maximum);

			String entryValueRepresentation = entryValueDecorator
					.representVariable();

			representation.append(entryKeyRepresentation);
			representation.append(" => ");
			representation.append(entryValueRepresentation);

			if (mapIterator.hasNext()) {

				representation.append(", ");

			}

			if (representation.length() > maximum) {

				return representation.toString();

			}

		}

		representation.append('}');

		return representation.toString();

	}

}