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

import edu.pitt.dbmi.fhir.resource.mapper.r4.IdentifierTypes;
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
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

/**
 * A utility for mapping Synthea patient data to FHIR patient resource.
 *
 * Apr 7, 2022 12:07:01 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class PatientResourceMapper extends AbstractSyntheaResource {

    private static final Meta US_CORE_PATIENT_PROFILE = new Meta();

    static {
        US_CORE_PATIENT_PROFILE.addProfile("http://hl7.org/fhir/us/core/STU4/StructureDefinition-us-core-patient.html");
    }

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
        patient.setMeta(US_CORE_PATIENT_PROFILE);
        patient.setIdentifier(getIdentifiers(fields));
        patient.setExtension(getExtensions(fields));
        patient.setName(getNames(fields));
        patient.setBirthDate(DateFormatters.YYYY_MM_DD.parse(fields[BIRTHDATE]));
        if (!fields[GENDER].isEmpty()) {
            patient.setGender(getGender(fields));
        }
        patient.addAddress(getAddress(fields));
        patient.setMaritalStatus(getMaritalStatus(fields));

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
     * @see https://www.hl7.org/fhir/r4/datatypes.html#Address
     */
    private static Address getAddress(String[] fields) {
        Address address = new Address();
        address.addLine(fields[ADDRESS])
                .setCity(fields[CITY])
                .setPostalCode(fields[ZIP])
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
     * @see https://www.hl7.org/fhir/r4/patient-definitions.html#Patient.name
     */
    private static List<HumanName> getNames(String[] fields) {
        List<HumanName> humanNames = new LinkedList<>();

        humanNames.add(getFullName(fields));
        if (!fields[MAIDEN].isEmpty()) {
            humanNames.add(getMaidenName(fields));
        }

        return humanNames;
    }

    /**
     *
     * @param fields
     * @return
     * @see https://www.hl7.org/fhir/r4/datatypes.html#HumanName
     */
    private static HumanName getMaidenName(String[] fields) {
        HumanName maidenName = new HumanName();
        maidenName.setUse(HumanName.NameUse.MAIDEN);
        maidenName.addGiven(fields[FIRST]);
        maidenName.setFamily(fields[MAIDEN]);
        if (!fields[PREFIX].isEmpty()) {
            maidenName.addPrefix(fields[PREFIX]);
        }
        if (!fields[SUFFIX].isEmpty()) {
            maidenName.addSuffix(fields[SUFFIX]);
        }

        return maidenName;
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
        name.addGiven(fields[FIRST]);
        name.setFamily(fields[LAST]);
        if (!fields[PREFIX].isEmpty()) {
            name.addPrefix(fields[PREFIX]);
        }
        if (!fields[SUFFIX].isEmpty()) {
            name.addSuffix(fields[SUFFIX]);
        }

        return name;
    }

    private static List<Identifier> getIdentifiers(String[] fields) {
        List<Identifier> identifiers = new LinkedList<>();

        identifiers.add((new Identifier())
                .setSystem(SYNTHEA_IDENTIFIER)
                .setValue(fields[ID]));

        identifiers.add((new Identifier())
                .setType(mapCodingToCodeableConcept(IdentifierTypes.MEDICAL_RECORD_NUMBER))
                .setSystem("http://hospital.smarthealthit.org")
                .setValue(fields[ID]));

        identifiers.add((new Identifier())
                .setType(mapCodingToCodeableConcept(IdentifierTypes.SOCIAL_SECURITY_NUMBER))
                .setSystem("http://hl7.org/fhir/sid/us-ssn")
                .setValue(fields[SSN]));

        if (!fields[DRIVERS].isEmpty()) {
            identifiers.add((new Identifier())
                    .setType(mapCodingToCodeableConcept(IdentifierTypes.DRIVERS_LICENSE_NUMBER))
                    .setSystem("urn:oid:2.16.840.1.113883.4.3.25")
                    .setValue(fields[DRIVERS]));
        }

        if (!fields[PASSPORT].isEmpty()) {
            identifiers.add((new Identifier())
                    .setType(mapCodingToCodeableConcept(IdentifierTypes.PASSPORT_NUMBER))
                    .setSystem(SHR_EXT + "passportNumber")
                    .setValue(fields[PASSPORT]));
        }

        return identifiers;
    }

    private static List<Extension> getExtensions(String[] fields) {
        List<Extension> extensions = new LinkedList<>();

        extensions.add(getRaceExtension(fields[RACE]));
        extensions.add(getEthnicityExtension(fields[ETHNICITY]));
        extensions.add(getBirthSexExtension(fields[GENDER]));

        return extensions;
    }

    private static Extension getBirthSexExtension(String gender) {
        Extension extension = new Extension("http://hl7.org/fhir/us/core/StructureDefinition/us-core-birthsex");

        switch (gender) {
            case "M":
                extension.setValue(new CodeType("M"));
                break;
            case "F":
                extension.setValue(new CodeType("F"));
                break;
        }

        return extension;
    }

    private static Extension getEthnicityExtension(String ethnicity) {
        String ethnicityDisplay = mapToFhirEthnicity(ethnicity);
        String ethnicityCode = mapToRaceEthnicityToCode(ethnicity);

        Extension extension = new Extension("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity");

        Extension textExtension = new Extension("text");
        textExtension.setValue(new StringType(ethnicityDisplay));
        extension.addExtension(textExtension);

        Extension codingExtension = new Extension("ombCategory");
        codingExtension.setValue(getEthnicityCoding(ethnicityDisplay, ethnicityCode));
        extension.addExtension(codingExtension);

        return extension;
    }

    private static Extension getRaceExtension(String race) {
        String raceDisplay = mapRaceToDisplay(race);
        String raceCode = mapToRaceEthnicityToCode(race);

        Extension extension = new Extension("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race");

        Extension textExtension = new Extension("text");
        textExtension.setValue(new StringType(raceDisplay));
        extension.addExtension(textExtension);

        Extension codingExtension = new Extension("ombCategory");
        codingExtension.setValue(getRaceCoding(raceDisplay, raceCode));
        extension.addExtension(codingExtension);

        return extension;
    }

    private static Coding getEthnicityCoding(String display, String code) {
        Coding coding = new Coding();
        coding.setSystem("urn:oid:2.16.840.1.113883.6.238");
        coding.setCode(code);
        coding.setDisplay(display);

        return coding;
    }

    private static Coding getRaceCoding(String display, String code) {
        Coding coding = new Coding();

        if (display.equals("Other")) {
            coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-NullFlavor");
            coding.setCode("UNK");
            coding.setDisplay("Unknown");
        } else {
            coding.setSystem("urn:oid:2.16.840.1.113883.6.238");
            coding.setCode(code);
            coding.setDisplay(display);
        }

        return coding;
    }

    private static String mapToRaceEthnicityToCode(String raceEthnicity) {
        switch (raceEthnicity) {
            case "white":
                return "2106-3";
            case "black":
                return "2054-5";
            case "asian":
                return "2028-9";
            case "native":
                return "1002-5";
            case "hawaiian":
                return "2076-8";
            case "hispanic":
                return "2135-2";
            case "nonhispanic":
                return "2186-5";
            default:
                return "2131-1";
        }
    }

    private static String mapToFhirEthnicity(String ethnicity) {
        if (ethnicity.equals("hispanic")) {
            return "Hispanic or Latino";
        } else {
            return "Not Hispanic or Latino";
        }
    }

    private static String mapRaceToDisplay(String race) {
        switch (race) {
            case "white":
                return "White";
            case "black":
                return "Black or African American";
            case "asian":
                return "Asian";
            case "native":
                return "American Indian or Alaska Native";
            case "hawaiian":
                return "Native Hawaiian or Other Pacific Islander";
            default:
                return "Other";
        }
    }

}
