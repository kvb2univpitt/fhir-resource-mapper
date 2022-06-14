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
import java.util.Map;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Jun 13, 2022 10:50:23 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ObservationResourceMapperTest {

    /**
     * Test of getEncountersFromFile method, of class ObservationResourceMapper.
     */
    @Test
    public void testGetEncountersFromFile() {
        Map<String, Patient> patients = getPatients();
        Map<String, Encounter> encounters = getEncounters(patients);

        Path file = Paths.get(getClass().getResource("/data/brainai/observations.tsv").getFile());
        List<Observation> observations = ObservationResourceMapper.getEncountersFromFile(file, Delimiters.TAB_DELIM, patients, encounters);

        int expected = 1;
        int actual = observations.size();
        Assertions.assertEquals(expected, actual);
    }

    private Map<String, Encounter> getEncounters(Map<String, Patient> patients) {
        Path file = Paths.get(getClass().getResource("/data/brainai/encounters.tsv").getFile());

        return EncounterResourceMapper.getEncountersFromFile(file, Delimiters.TAB_DELIM, getPatients());
    }

    private Map<String, Patient> getPatients() {
        Path file = Paths.get(getClass().getResource("/data/brainai/persons.tsv").getFile());

        return PatientResourceMapper.getPatients(file, Delimiters.TAB_DELIM);
    }

}
