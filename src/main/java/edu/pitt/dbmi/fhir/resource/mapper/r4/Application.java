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
package edu.pitt.dbmi.fhir.resource.mapper.r4;

import edu.pitt.dbmi.fhir.resource.mapper.util.Delimiters;
import edu.pitt.dbmi.fhir.resource.mapper.util.JsonResourceConverterR4;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

/**
 *
 * Apr 28, 2022 12:25:52 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class Application {

    public static void main(String[] args) {
        if (args.length == 3) {
            Path inputDir = Paths.get(args[1]);
            Path outputDir = Paths.get(args[2]);
            if (isValidDirectory(inputDir) && isValidDirectory(outputDir)) {
                switch (args[0].trim().toLowerCase()) {
                    case "synthea":
                        exportSyntheaData(inputDir, outputDir);
                        break;
                    default:
                        System.err.printf("No such data mapper for %s exist.%n", args[0]);
                        System.exit(-1);
                }
            }
        } else {
            System.err.printf("java -jar %s <synthea|project> <data-directory> <output-directory>%n", getJarFileName());
        }
    }

    private static void exportPatientResources(Path outputDir, List<Patient> patients) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir.toString(), "patient_collection_bundle.json"))) {
            writer.write(JsonResourceConverterR4.patientsToJsonBundle(Bundle.BundleType.COLLECTION, patients, true));
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    private static void exportEncounterResources(Path outputDir, List<Encounter> encounters) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir.toString(), "encounter_collection_bundle.json"))) {
            writer.write(JsonResourceConverterR4.encountersToJsonBundle(Bundle.BundleType.COLLECTION, encounters, true));
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    private static void exportObservationResources(Path outputDir, List<Observation> observations) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir.toString(), "observation_collection_bundle.json"))) {
            writer.write(JsonResourceConverterR4.observationsToJsonBundle(Bundle.BundleType.COLLECTION, observations, true));
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    private static void exportSyntheaData(Path inputDir, Path outputDir) {
        try {
            Files.list(inputDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String filename = file.getFileName().toString();
                        if (filename.startsWith("patients")) {
                            List<Patient> patients = new LinkedList<>();
                            if (filename.endsWith(".csv")) {
                                patients.addAll(edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.PatientResourceMapper.getPatientsFromFile(file, Delimiters.COMMA_DELIM));
                            } else if (filename.endsWith(".tsv")) {
                                patients.addAll(edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.PatientResourceMapper.getPatientsFromFile(file, Delimiters.TAB_DELIM));
                            }

                            exportPatientResources(outputDir, patients);
                        } else if (filename.startsWith("encounters")) {
                            List<Encounter> encounters = new LinkedList<>();
                            if (filename.endsWith(".csv")) {
                                encounters.addAll(edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.EncounterResourceMapper.getEncountersFromFile(file, Delimiters.COMMA_DELIM));
                            } else if (filename.endsWith(".tsv")) {
                                encounters.addAll(edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.EncounterResourceMapper.getEncountersFromFile(file, Delimiters.TAB_DELIM));
                            }

                            exportEncounterResources(outputDir, encounters);
                        } else if (filename.startsWith("observations")) {
                            List<Observation> observations = new LinkedList<>();
                            if (filename.endsWith(".csv")) {
                                observations.addAll(edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.ObservationResourceMapper.getObservationFromFile(file, Delimiters.COMMA_DELIM));
                            } else if (filename.endsWith(".tsv")) {
                                observations.addAll(edu.pitt.dbmi.fhir.resource.mapper.r4.synthea.ObservationResourceMapper.getObservationFromFile(file, Delimiters.TAB_DELIM));
                            }

                            exportObservationResources(outputDir, observations);
                        }
                    });
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    private static boolean isValidDirectory(Path dir) {
        if (!Files.exists(dir)) {
            System.err.printf("Directory '%s' does not exist.%n", dir);

            return false;
        }

        if (!Files.isDirectory(dir)) {
            System.err.printf("'%s' is not a directory.%n", dir);

            return false;
        }

        return true;
    }

    private static String jarTitle() {
        return Application.class.getPackage().getImplementationTitle();
    }

    private static String jarVersion() {
        String version = Application.class.getPackage().getImplementationVersion();

        return (version == null) ? "unknown" : version;
    }

    private static String getJarFileName() {
        return String.format("%s-%s.jar", jarTitle(), jarVersion());
    }

}
