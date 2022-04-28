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
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Apr 27, 2022 3:40:07 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ObservationResourceMapperTest {

    /**
     * Test of getEncountersFromFile method, of class ObservationResourceMapper.
     */
    @Test
    public void testGetEncountersFromFile() {
        Path file = Paths.get(getClass().getResource("/data/synthea/covid19_csv/observations.csv").getFile());
        List<Observation> observations = ObservationResourceMapper.getObservationFromFile(file, Delimiters.COMMA_DELIM);

        int expected = 779;
        int actual = observations.size();
        Assertions.assertEquals(expected, actual);
    }

}
