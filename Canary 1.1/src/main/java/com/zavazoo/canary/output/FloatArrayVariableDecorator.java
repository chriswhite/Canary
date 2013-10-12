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
 * Variable decorator that represents a variable that is an array of
 * floating-point numbers using the following notation: [float1, float2, float3]
 * 
 * @author Chris White <chriswhitelondon@gmail.com>
 * @since JDK6
 */
public class FloatArrayVariableDecorator extends VariableDecorator {

	/**
	 * Represents the variable that is an array of floating-point numbers.
	 * 
	 * @return the representation.
	 */
	public String representVariable() {

		float[] floatArray = (float[]) getVariable();

		int floatArrayLength = floatArray.length;

		Float[] objectArray = new Float[floatArrayLength];

		for (int index = 0; index < floatArrayLength; index++) {

			objectArray[index] = floatArray[index];

		}

		VariableDecorator arrayDecorator = new PrimitiveArrayVariableDecorator();
		arrayDecorator.setVariable(objectArray);
		arrayDecorator.setMaximum(getMaximum());

		return arrayDecorator.representVariable();

	}

}