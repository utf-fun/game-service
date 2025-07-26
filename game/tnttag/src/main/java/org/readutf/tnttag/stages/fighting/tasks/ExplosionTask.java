package org.readutf.tnttag.stages.fighting.tasks;

import java.util.stream.IntStream;
import org.readutf.engine.GameException;
import org.readutf.engine.minestom.schedular.CountdownTask;
import org.readutf.tnttag.stages.fighting.FightingStage;
import org.readutf.tnttag.systems.TntHolderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplosionTask extends CountdownTask {

    private static final Logger log = LoggerFactory.getLogger(ExplosionTask.class);
    private final TntHolderManager tntHolderManager;

    public ExplosionTask(TntHolderManager tntHolderManager) {
        super(FightingStage.EXPLOSION_DURATION, IntStream.range(0, 15).boxed().toList());
        this.tntHolderManager = tntHolderManager;
    }

    @Override
    public void handleInterval(int interval) {
        if (interval == 0) {
            log.info("Explosion task finished.");
            try {
                tntHolderManager.explode();
            } catch (GameException e) {
                log.error("", e);
            }
        }
    }

}
