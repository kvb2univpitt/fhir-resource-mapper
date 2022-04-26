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
package edu.pitt.dbmi.fhir.resource.mapper.r4.synthea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Apr 25, 2022 8:50:44 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class LocationsTest {

    /**
     * Test of getStateAbbreviation method, of class Locations.
     */
    @Test
    public void testGetStateAbbreviation() {
        String expected = "PA";
        String actual = Locations.getStateAbbreviation("Pennsylvania");
        Assertions.assertEquals(expected, actual);
    }

}
