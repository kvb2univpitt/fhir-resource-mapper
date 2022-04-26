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
package edu.pitt.dbmi.fhir.resource.mapper.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;

/**
 * A utility for converting a FHIR R4 resource object to a JSON representation
 * and vice-versa.
 *
 * Apr 7, 2022 2:41:57 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class JsonResourceConverterR4 {

    private static final IParser PARSER = FhirContext.forR4().newJsonParser();

    private JsonResourceConverterR4() {
    }

    public static String toString(Patient patient, boolean printPretty) {
        PARSER.setPrettyPrint(printPretty);
        return PARSER.encodeResourceToString(patient);
    }

    public static String toString(Encounter encounter, boolean printPretty) {
        PARSER.setPrettyPrint(printPretty);

        return PARSER.encodeResourceToString(encounter);
    }

    public static Patient toPatient(String json) {
        return PARSER.parseResource(Patient.class, json);
    }

    public static Encounter toEncounter(String json) {
        return PARSER.parseResource(Encounter.class, json);
    }

}
