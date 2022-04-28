/*
 * Copyright (C) 2022 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.fhir.resource.mapper.r4;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * Apr 28, 2022 1:22:13 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ApplicationTest {

    /**
     * Test of main method, of class Application.
     */
    @Disabled
    @Test
    public void testMain() {
        String[] args = {
            "synthea",
            "src/test/resources/data/synthea/covid19_csv",
            "src/test/resources/data/synthea/output"
        };
        Application.main(args);
    }

}
