package com.li.battle.projectile;

import com.li.battle.core.scene.BattleScene;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 子弹容器
 * @author li-yuanwen
 * @date 2022/5/30
 */
public class ProjectileManager {

    /** 关联的战斗场景 **/
    private final BattleScene scene;

    /** 子弹容器 **/
    private final List<Projectile> projectiles = new LinkedList<>();

    public ProjectileManager(BattleScene scene) {
        this.scene = scene;
    }


    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void update() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while(iterator.hasNext()) {
            Projectile next = iterator.next();
            if (next.checkFinish()) {
                iterator.remove();
                continue;
            }

            next.updatePosition();
            next.tryHit();

            if (next.checkFinish()) {
                iterator.remove();
            }
        }
    }


}
