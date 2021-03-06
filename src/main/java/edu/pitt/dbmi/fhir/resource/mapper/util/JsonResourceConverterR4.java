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

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import java.io.Reader;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
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

    public static String resourceToJson(IBaseResource resource) {
        return resourceToJson(resource, true);
    }

    public static String resourceToJson(IBaseResource resource, boolean printPretty) {
        PARSER.setPrettyPrint(printPretty);
        return PARSER.encodeResourceToString(resource);
    }

    public static IBaseResource parseResource(Reader reader) throws ConfigurationException, DataFormatException {
        return PARSER.parseResource(reader);
    }

    public static String patientsToJsonBundle(Bundle.BundleType type, List<Patient> patients, boolean printPretty) {
        Bundle bundle = new Bundle();
        bundle.setType(type);

        patients.forEach(e -> bundle.addEntry().setResource(e));

        PARSER.setPrettyPrint(printPretty);

        return PARSER.encodeResourceToString(bundle);
    }

    public static String encountersToJsonBundle(Bundle.BundleType type, List<Encounter> encounters, boolean printPretty) {
        Bundle bundle = new Bundle();
        bundle.setType(type);

        encounters.forEach(e -> bundle.addEntry().setResource(e));

        PARSER.setPrettyPrint(printPretty);

        return PARSER.encodeResourceToString(bundle);
    }

    public static String observationsToJsonBundle(Bundle.BundleType type, List<Observation> observations, boolean printPretty) {
        Bundle bundle = new Bundle();
        bundle.setType(type);

        observations.forEach(e -> bundle.addEntry().setResource(e));

        PARSER.setPrettyPrint(printPretty);

        return PARSER.encodeResourceToString(bundle);
    }

    public static Patient toPatient(String json) {
        return PARSER.parseResource(Patient.class, json);
    }

    public static Encounter toEncounter(String json) {
        return PARSER.parseResource(Encounter.class, json);
    }

    public static Observation toObservation(String json) {
        return PARSER.parseResource(Observation.class, json);
    }

}
