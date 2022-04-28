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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;

/**
 * A utility for mapping Synthea encounter data to FHIR encounter resource.
 *
 * Apr 24, 2022 7:44:57 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 * @see https://www.hl7.org/fhir/encounter.html
 */
public class EncounterResourceMapper extends AbstractSyntheaResource {

    private static final Meta US_CORE_ENCOUNTER_PROFILE = new Meta();

    static {
        US_CORE_ENCOUNTER_PROFILE.addProfile("http://hl7.org/fhir/us/core/StructureDefinition/us-core-encounter");
    }

    private static final int ID = 0;
    private static final int START = 1;
    private static final int STOP = 2;
    private static final int PATIENT = 3;
    private static final int ORGANIZATION = 4;
    private static final int PROVIDER = 5;
    private static final int PAYER = 6;
    private static final int ENCOUNTERCLASS = 7;
    private static final int CODE = 8;
    private static final int DESCRIPTION = 9;
    private static final int BASE_ENCOUNTER_COST = 10;
    private static final int TOTAL_CLAIM_COST = 11;
    private static final int PAYER_COVERAGE = 12;
    private static final int REASONCODE = 13;
    private static final int REASONDESCRIPTION = 14;

    private static final String[] HEADERS = {
        "Id",
        "START",
        "STOP",
        "PATIENT",
        "ORGANIZATION",
        "PROVIDER",
        "PAYER",
        "ENCOUNTERCLASS",
        "CODE",
        "DESCRIPTION",
        "BASE_ENCOUNTER_COST",
        "TOTAL_CLAIM_COST",
        "PAYER_COVERAGE",
        "REASONCODE",
        "REASONDESCRIPTION"
    };

    public static List<Encounter> getEncountersFromFile(final Path file, final Pattern delimiter) {
        List<Encounter> encounters = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            reader.readLine(); // skip header
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                encounters.add(getEncounter(delimiter.split(line.trim() + ",end")));
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
    private static Encounter getEncounter(String[] fields) throws ParseException {
        Encounter encounter = new Encounter();
        encounter.setMeta(US_CORE_ENCOUNTER_PROFILE);
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);
        encounter.addType(getType(fields));
        encounter.setClass_(getClassCode(fields));
        encounter.setPeriod(getPeriod(fields));
        if (!fields[REASONCODE].isEmpty()) {
            encounter.addReasonCode(getReasonCode(fields));
        }
        encounter.setSubject(getSubject(fields));

        return encounter;
    }

    private static CodeableConcept getType(String[] fields) {
        CodeableConcept type = new CodeableConcept();

        if (fields[CODE].isEmpty()) {
            type.addCoding()
                    .setCode("185349003")
                    .setDisplay("Encounter for check up")
                    .setSystem(SNOMED_URI);
        } else {
            type.addCoding()
                    .setCode(fields[CODE])
                    .setDisplay(fields[DESCRIPTION])
                    .setSystem(SNOMED_URI);
        }

        return type;
    }

    private static CodeableConcept getReasonCode(String[] fields) {
        CodeableConcept reasonCode = new CodeableConcept();
        reasonCode.addCoding()
                .setCode(fields[REASONCODE])
                .setDisplay(fields[REASONDESCRIPTION])
                .setSystem(SNOMED_URI);

        return reasonCode;
    }

    private static Period getPeriod(String[] fields) {
        Date start = new Date(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(fields[START])).toEpochMilli());
        Date end = new Date(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(fields[STOP])).toEpochMilli());

        return (new Period()).setStart(start).setEnd(end);
    }

    private static Coding getClassCode(String[] fields) {
        Coding classCode = new Coding();
        classCode.setCode(EncounterType.fromString(fields[ENCOUNTERCLASS]).code());
        classCode.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");

        return classCode;
    }

    private static Reference getSubject(String[] fields) {
        return (new Reference())
                .setReference(String.format("urn:uuid:%s", fields[PATIENT]));
    }

}
