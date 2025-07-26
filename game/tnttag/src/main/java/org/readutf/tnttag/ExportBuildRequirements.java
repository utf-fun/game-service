package org.readutf.tnttag;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.readutf.buildformat.common.exception.BuildFormatException;
import org.readutf.buildformat.common.format.BuildFormatManager;
import org.readutf.buildformat.common.format.requirements.RequirementData;
import org.readutf.tnttag.positions.TagPositions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExportBuildRequirements {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws BuildFormatException, IOException {
        List<RequirementData> requirements = BuildFormatManager.getValidators(TagPositions.class);

        objectMapper.writeValue(new File("tnttag.json"), requirements);
    }
}
