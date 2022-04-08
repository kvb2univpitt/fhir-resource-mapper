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

import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

/**
 * A utility for mapping Synthea patient data to FHIR patient resource.
 *
 * Apr 7, 2022 12:07:01 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class PatientResourceMapper {

    public static final int ID = 0;
    public static final int BIRTHDATE = 1;
    public static final int DEATHDATE = 2;
    public static final int SSN = 3;
    public static final int DRIVERS = 4;
    public static final int PASSPORT = 5;
    public static final int PREFIX = 6;
    public static final int FIRST = 7;
    public static final int LAST = 8;
    public static final int SUFFIX = 9;
    public static final int MAIDEN = 10;
    public static final int MARITAL = 11;
    public static final int RACE = 12;
    public static final int ETHNICITY = 13;
    public static final int GENDER = 14;
    public static final int BIRTHPLACE = 15;
    public static final int ADDRESS = 16;
    public static final int CITY = 17;
    public static final int STATE = 18;
    public static final int COUNTY = 19;
    public static final int ZIP = 20;
    public static final int LAT = 21;
    public static final int LON = 22;
    public static final int HEALTHCARE_EXPENSES = 23;
    public static final int HEALTHCARE_COVERAGE = 24;

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

    /**
     *
     * @param fields
     * @return
     * @throws ParseException
     * @see https://www.hl7.org/fhir/r4/patient.html
     */
    private static Patient getPatient(String[] fields) throws ParseException {
        Patient patient = new Patient();
        patient.setId(fields[ID]);
        patient.setName(Collections.singletonList(getName(fields)));
        patient.setAddress(Collections.singletonList(getAddress(fields)));
        patient.setGender(getGender(fields));
        patient.setMaritalStatus(getMaritalStatus(fields));
        patient.setBirthDate(DateFormatters.YYYY_MM_DD.parse(fields[BIRTHDATE]));

        return patient;
    }

    /**
     *
     * @param fields
     * @return
     * @see
     * https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.maritalStatus
     */
    private static CodeableConcept getMaritalStatus(String[] fields) {
        Coding coding = new Coding();
        switch (fields[MARITAL]) {
            case "S":
                coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus");
                coding.setDisplay("Never Married");
                coding.setCode(fields[MARITAL]);
                break;
            case "M":
                coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus");
                coding.setDisplay("Married");
                coding.setCode(fields[MARITAL]);
                break;
            default:
                coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-NullFlavor");
                coding.setDisplay("unknown");
                coding.setCode("UNK");
                break;
        }

        return new CodeableConcept(coding);
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.gender
     */
    private static Enumerations.AdministrativeGender getGender(String[] fields) {
        switch (fields[GENDER]) {
            case "M":
                return Enumerations.AdministrativeGender.MALE;
            case "F":
                return Enumerations.AdministrativeGender.FEMALE;
            default:
                return Enumerations.AdministrativeGender.UNKNOWN;
        }
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/datatypes.html#Address
     */
    private static Address getAddress(String[] fields) {
        Address address = new Address();
        address.setCity(fields[CITY]);
        address.setCountry(fields[COUNTY]);
        address.setPostalCode(fields[ZIP]);
        address.setState(fields[STATE]);
        address.setLine(Collections.singletonList(new StringType(fields[ADDRESS])));

        return address;
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/datatypes.html#HumanName
     */
    private static HumanName getName(String[] fields) {
        HumanName name = new HumanName();
        name.setSuffix(Collections.singletonList(new StringType(fields[SUFFIX])));
        name.setFamily(fields[LAST]);
        name.setGiven(Collections.singletonList(new StringType(fields[FIRST])));

        return name;
    }

}
