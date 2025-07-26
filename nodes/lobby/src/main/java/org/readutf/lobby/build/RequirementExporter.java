package org.readutf.lobby.build;

import com.google.gson.Gson;
import org.readutf.buildformat.common.exception.BuildFormatException;
import org.readutf.buildformat.common.format.BuildFormatManager;
import org.readutf.buildformat.common.format.requirements.RequirementData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RequirementExporter {

    public static void main(String[] args) throws BuildFormatException, IOException {
        List<RequirementData> validators = BuildFormatManager.getValidators(LobbyPositions.class);

        System.out.println(validators);

        Gson gson = new Gson();

        FileWriter writer = new FileWriter("requirements.json");
        gson.toJson(validators, writer);
        writer.flush();
        writer.close();
    }

}
