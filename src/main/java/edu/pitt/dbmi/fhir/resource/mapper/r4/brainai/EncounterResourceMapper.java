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

import edu.pitt.dbmi.fhir.resource.mapper.r4.IdentifierTypes;
import edu.pitt.dbmi.fhir.resource.mapper.r4.standards.CodingSystemURIs;
import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import edu.pitt.dbmi.fhir.resource.mapper.util.FhirUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;

/**
 *
 * Jun 7, 2022 9:51:07 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class EncounterResourceMapper {

    public static final int ENCOUNTER_ID = 0;
    private static final int START = 1;
    private static final int STOP = 2;
    public static final int PERSON_ID = 3;
    private static final int CODE = 4;
    private static final int DESCRIPTION = 5;
    private static final int REASONCODE = 6;
    private static final int REASONDESCRIPTION = 7;

    public static Map<String, Encounter> getEncountersFromFile(final Path file, final Pattern delimiter, Map<String, Patient> patients) {
        Map<String, Encounter> encounters = new HashMap<>();

        try ( BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            reader.readLine(); // skip header
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] fields = delimiter.split(line.trim());
                Patient patient = patients.get(fields[PERSON_ID]);
                if (patient != null) {
                    encounters.put(fields[ENCOUNTER_ID], getEncounter(fields, patient));
                }
            }
        } catch (IOException | ParseException exception) {
            exception.printStackTrace(System.err);
        }

        return encounters;
    }

    /**
     *
     * @param fields
     * @return
     * @throws ParseException
     * @see https://www.hl7.org/fhir/r4/encounter.html
     */
    private static Encounter getEncounter(String[] fields, Patient patient) throws ParseException {
        Encounter encounter = new Encounter();
        encounter.setIdentifier(getIdentifiers(fields));
        encounter.setSubject(getPatientReference(patient));
        encounter.setPeriod(getPeriod(fields));
        encounter.addType(getType(fields));
        encounter.addReasonCode(getReasonCode(fields));

        return encounter;
    }

    private static CodeableConcept getReasonCode(String[] fields) {
        CodeableConcept reasonCode = new CodeableConcept();
        reasonCode.addCoding()
                .setCode(fields[REASONCODE])
                .setDisplay(fields[REASONDESCRIPTION])
                .setSystem(CodingSystemURIs.SNOMED_URI);

        return reasonCode;
    }

    private static CodeableConcept getType(String[] fields) {
        CodeableConcept type = new CodeableConcept();

        type.addCoding()
                .setCode(fields[CODE])
                .setDisplay(fields[DESCRIPTION])
                .setSystem(CodingSystemURIs.SNOMED_URI);

        return type;
    }

    private static Period getPeriod(String[] fields) throws ParseException {
        Date start = DateFormatters.MM_DD_YYYY_HHMMSS_AM.parse(fields[START]);
        Date end = DateFormatters.MM_DD_YYYY_HHMMSS_AM.parse(fields[STOP]);

        return (new Period()).setStart(start).setEnd(end);
    }

    private static Reference getPatientReference(Patient patient) {
        return new Reference()
                .setReference("Patient/" + patient.getIdentifierFirstRep().getValue())
                .setDisplay(patient.getNameFirstRep().getNameAsSingleString());
    }

    private static List<Identifier> getIdentifiers(String[] fields) {
        return Collections.singletonList(new Identifier()
                .setType(FhirUtils.mapCodingToCodeableConcept(IdentifierTypes.CERNER_ENCOUNTER_ID))
                .setSystem("urn:oid:2.16.840.1.113883.3.552")
                .setValue(fields[ENCOUNTER_ID]));
    }

}
