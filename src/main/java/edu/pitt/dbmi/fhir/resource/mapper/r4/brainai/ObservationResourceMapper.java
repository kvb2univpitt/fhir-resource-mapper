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

import edu.pitt.dbmi.fhir.resource.mapper.r4.standards.CodingSystemURIs;
import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import edu.pitt.dbmi.fhir.resource.mapper.util.FhirUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;

/**
 *
 * Jun 13, 2022 10:36:00 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ObservationResourceMapper {

    private static final int DATE = 0;
    private static final int PATIENT = 1;
    private static final int ENCOUNTER = 2;
    private static final int CODE = 3;
    private static final int DESCRIPTION = 4;
    private static final int VALUE = 5;
    private static final int UNITS = 6;
    private static final int TYPE = 7;
    private static final int CATEGORY = 8;

    public static List<Observation> getObservations(final Path file, final Pattern delimiter) {
        List<Observation> observations = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            reader.readLine(); // skip header
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                observations.add(getObservation(delimiter.split(line.trim())));
            }
        } catch (IOException | ParseException exception) {
            exception.printStackTrace(System.err);
        }

        return observations;
    }

    /**
     *
     * @param fields
     * @return
     * @throws ParseException
     * @see https://www.hl7.org/fhir/observation.html
     */
    public static Observation getObservation(String[] fields) throws ParseException {
        Observation observation = new Observation();
        observation.setSubject(getSubject(fields));
        observation.setEncounter(getEncounter(fields));
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation.setCode(getCode(fields));
        observation.addComponent(getComponent(fields));
        observation.setEffective(new DateTimeType(DateFormatters.MM_DD_YYYY_HHMMSS_AM.parse(fields[DATE])));
        observation.addCategory(getCategory(fields));

        return observation;
    }

    private static CodeableConcept getCategory(String[] fields) {
        Coding category = (new Coding())
                .setCode(fields[CATEGORY])
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setDisplay(fields[CATEGORY]);

        return new CodeableConcept(category);
    }

    private static Observation.ObservationComponentComponent getComponent(String[] fields) {
        Observation.ObservationComponentComponent comp = new Observation.ObservationComponentComponent();
        comp.setCode(getCode(fields));
        comp.setValue(FhirUtils.toFHIRType(fields[TYPE], fields[VALUE], fields[UNITS]));

        return comp;
    }

    private static CodeableConcept getCode(String[] fields) {
        return new CodeableConcept(new Coding(CodingSystemURIs.LOINC_URI, fields[CODE], fields[DESCRIPTION]));
    }

    private static Reference getEncounter(String[] fields) {
        return (new Reference())
                .setReference(fields[ENCOUNTER]);
    }

    private static Reference getSubject(String[] fields) {
        return new Reference()
                .setReference(fields[PATIENT]);
    }

}
