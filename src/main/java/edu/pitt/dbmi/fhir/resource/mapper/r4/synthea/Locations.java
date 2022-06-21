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

import edu.pitt.dbmi.fhir.resource.mapper.util.Delimiters;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * Apr 25, 2022 8:48:01 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class Locations {

    private static final Map<String, String> stateAbbreviations = new HashMap<>();

    static {
        loadStateAbbreviations();
    }

    private Locations() {
    }

    public static String getStateAbbreviation(String state) {
        return stateAbbreviations.get(state);
    }

    private static void loadStateAbbreviations() {
        Path file = Paths.get(Locations.class.getResource("/synthea/zipcodes.csv").getFile());
        if (Files.exists(file)) {
            try (Stream<String> stream = Files.lines(file)) {
                int stateNameIndex = 1;
                int stateAbbrevIndex = 2;
                stream.forEach(line -> {
                    String[] fields = Delimiters.COMMA_DELIM.split(line.trim());
                    if (fields.length > 2) {
                        stateAbbreviations.put(fields[stateNameIndex].trim(), fields[stateAbbrevIndex].trim());
                    }
                });
            } catch (IOException exception) {
                exception.printStackTrace(System.err);
            }
        }
    }

}
