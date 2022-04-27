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
package edu.pitt.dbmi.fhir.resource.mapper.r4.project;

import edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.Locations;
import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;

/**
 *
 * Apr 26, 2022 11:55:13 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class PatientResourceMapper {

    private static final Meta US_CORE_PATIENT_PROFILE = new Meta();

    static {
        US_CORE_PATIENT_PROFILE.addProfile("http://hl7.org/fhir/us/core/STU4/StructureDefinition-us-core-patient.html");
    }

    public static final int ENCNTR_ID = 0;
    public static final int NAME_LAST = 1;
    public static final int NAME_FIRST = 2;
    public static final int SEX = 3;
    public static final int BIRTH_DT_TM = 4;
    public static final int AGE_DAYS = 5;
    public static final int STREET_ADDR = 6;
    public static final int CITY = 7;
    public static final int STATE = 8;
    public static final int ZIPCODE = 9;

    public static List<Patient> getPatientsFromFile(final Path file, final Pattern delimiter) {
        List<Patient> patients = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            reader.readLine(); // skip header
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                patients.add(getPatient(delimiter.split(line.trim())));
            }
        } catch (IOException | ParseException exception) {
            exception.printStackTrace(System.err);
        }

        return patients;
    }

    private static Patient getPatient(String[] fields) throws ParseException {
        Patient patient = new Patient();
        patient.setMeta(US_CORE_PATIENT_PROFILE);
        patient.setName(getNames(fields));
        patient.setBirthDate(DateFormatters.MM_DD_YYYY.parse(fields[BIRTH_DT_TM]));
        if (!fields[SEX].isEmpty()) {
            patient.setGender(getGender(fields));
        }

        return patient;
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/datatypes.html#Address
     */
    private static Address getAddress(String[] fields) {
        Address address = new Address();
        address.addLine(fields[STREET_ADDR])
                .setCity(fields[CITY])
                .setPostalCode(fields[ZIPCODE])
                .setState(Locations.getStateAbbreviation(fields[STATE]))
                .setCountry("USA");

        return address;
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.gender
     */
    private static Enumerations.AdministrativeGender getGender(String[] fields) {
        switch (fields[SEX]) {
            case "Male":
                return Enumerations.AdministrativeGender.MALE;
            case "Female":
                return Enumerations.AdministrativeGender.FEMALE;
            default:
                return Enumerations.AdministrativeGender.UNKNOWN;
        }
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.name
     */
    private static List<HumanName> getNames(String[] fields) {
        List<HumanName> humanNames = new LinkedList<>();

        humanNames.add(getFullName(fields));

        return humanNames;
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/datatypes.html#HumanName
     */
    private static HumanName getFullName(String[] fields) {
        HumanName name = new HumanName();
        name.setUse(HumanName.NameUse.OFFICIAL);
        name.addGiven(fields[NAME_FIRST]);
        name.setFamily(fields[NAME_LAST]);

        return name;
    }

}
