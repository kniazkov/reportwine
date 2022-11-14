/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Polina Volkhontseva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cqfn.reportwine.model;

import java.util.LinkedList;
import java.util.List;
import org.cqfn.reportwine.exceptions.ExpectedTextArray;

/**
 * An array of values.
 *
 * @since 0.1
 */
public final class Array implements Value {
    /**
     * The linked list of values.
     */
    private final List<Value> values;

    /**
     * Constructor.
     * @param items The list of values
     */
    public Array(final List<Value> items) {
        this.values = new LinkedList<>(items);
    }

    /**
     * Checks if the array is a list of texts.
     * @return Checking result, {@code true} if the array is a text array
     *  or {@code false} otherwise
     * @throws ExpectedTextArray If text array expected, but other objects found in the array
     */
    public boolean isTextArray() throws ExpectedTextArray {
        final boolean text = this.values.get(0) instanceof Text;
        if (text) {
            int idx = 1;
            while (idx < this.values.size()) {
                if (this.values.get(idx) instanceof Text) {
                    idx += 1;
                } else {
                    throw new ExpectedTextArray(this.toString());
                }
            }
        }
        return text;
    }

    /**
     * Checks if the array is a list of arrays, i.e, a structure
     * that specifies a table.
     * @return Checking result, {@code true} if the array is a table
     *  or {@code false} otherwise
     */
    public boolean isTable() {
        return this.values.get(0) instanceof Array;
    }

    @Override
    public List<Value> getValue() {
        return this.values;
    }
}
