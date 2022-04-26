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

import edu.pitt.dbmi.fhir.resource.mapper.util.Delimiters;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.Encounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Apr 25, 2022 12:53:54 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class EncounterResourceMapperTest {

    /**
     * Test of getPatientsFromFile method, of class EncounterResourceMapper.
     */
    @Test
    public void testGetPatientsFromFile() {
        Path file = Paths.get(getClass().getResource("/data/synthea/covid19_csv/encounters.csv").getFile());
        Pattern delimiter = Delimiters.COMMA_DELIM;
        List<Encounter> encounters = EncounterResourceMapper.getEncountersFromFile(file, delimiter);

        int expected = 57;
        int actual = encounters.size();
        Assertions.assertEquals(expected, actual);
    }

}
