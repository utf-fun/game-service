package org.readutf.tnttag.stages.fighting.tasks;

import java.time.Duration;
import java.util.Collections;
import org.readutf.engine.minestom.schedular.CountdownTask;
import org.readutf.tnttag.stages.fighting.FightingStage;

public class FightingWarmupTask extends CountdownTask {

    private final FightingStage fightingStage;

    /**
     * Creates a new CountdownTask that will run for the specified duration.
     *
     * @param fightingStage The FightingStage instance associated with this task.
     */
    public FightingWarmupTask(FightingStage fightingStage) {
        super(Duration.ofSeconds(3), Collections.emptyList());
        this.fightingStage = fightingStage;
    }

    @Override
    public void handleInterval(int interval) {
        if(interval == 0) {
            fightingStage.selectChasers();
        }
    }
}
