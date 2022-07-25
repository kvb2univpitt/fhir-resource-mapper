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
package edu.pitt.dbmi.fhir.resource.mapper.r4.brainai;

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
 * Jun 7, 2022 9:53:42 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class EncounterResourceMapperTest {

    /**
     * Test of getEncountersFromFile method, of class EncounterResourceMapper.
     */
    @Test
    public void testGetEncountersFromFile() {
        Path file = Paths.get(getClass().getResource("/data/brainai/encounters.tsv").getFile());
        Pattern delimiter = Delimiters.TAB_DELIM;
        List<Encounter> encounters = EncounterResourceMapper.getEncounters(file, delimiter);
//        System.out.println("================================================================================");
//        encounters.stream()
//                .map(e -> JsonResourceConverterR4.resourceToJson(e, true))
//                .forEach(System.out::println);
//        System.out.println("================================================================================");

        int expected = 46;
        int actual = encounters.size();
        Assertions.assertEquals(expected, actual);
    }

}
