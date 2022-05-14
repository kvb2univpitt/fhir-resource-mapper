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
import edu.pitt.dbmi.fhir.resource.mapper.r4.standards.ResourceProfiles;
import edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.Locations;
import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import edu.pitt.dbmi.fhir.resource.mapper.util.FhirUtils;
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
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

/**
 *
 * May 13, 2022 4:21:28 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 * @see https://www.hl7.org/fhir/r4/patient.html
 */
public class PatientResourceMapper {

    public static final int PERSON_ID = 0;
    public static final int BIRTHDATE = 1;
    public static final int FIRST_NAME = 2;
    public static final int LAST_NAME = 3;
    public static final int GENDER = 4;
    public static final int ADDRESS = 5;
    public static final int CITY = 6;
    public static final int STATE = 7;
    public static final int ZIP = 8;

    /**
     * @param file
     * @param delimiter
     * @return
     */
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
        patient.setMeta(ResourceProfiles.US_CORE_PATIENT_PROFILE);
        patient.setIdentifier(getIdentifiers(fields));
        patient.setName(getNames(fields));
        patient.setGender(getGender(fields));
        patient.setAddress(getAddress(fields));
        patient.setBirthDate(DateFormatters.MM_DD_YYYY.parse(fields[BIRTHDATE]));
        System.out.println(fields[BIRTHDATE]);

        return patient;
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/datatypes.html#Address
     */
    private static List<Address> getAddress(String[] fields) {
        return Collections.singletonList(new Address()
                .setCity(fields[CITY])
                .setPostalCode(fields[ZIP])
                .setState(Locations.getStateAbbreviation(fields[STATE].toUpperCase()))
                .setCountry("USA"));
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.gender
     */
    private static Enumerations.AdministrativeGender getGender(String[] fields) {
        String gender = fields[GENDER].toLowerCase();
        switch (gender) {
            case "male":
                return Enumerations.AdministrativeGender.MALE;
            case "female":
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
        List<HumanName> names = new LinkedList<>();
        names.add(new HumanName()
                .setUse(HumanName.NameUse.OFFICIAL)
                .addGiven(fields[FIRST_NAME])
                .setFamily(fields[LAST_NAME]));

        return names;
    }

    /**
     * Patient's identifier.
     *
     * @param fields
     * @return
     * @see
     * https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.identifier
     */
    private static List<Identifier> getIdentifiers(String[] fields) {
        return Collections.singletonList(new Identifier()
                .setType(FhirUtils.mapCodingToCodeableConcept(IdentifierTypes.CERNER_PERSON_ID, IdentifierTypes.MEDICAL_RECORD_NUMBER))
                .setSystem("urn:oid:2.16.840.1.113883.6.1000")
                .setValue(fields[PERSON_ID]));
    }

}
