package screen;


import engine.*;
import entity.Ship;
import sound.SoundPlay;
import sound.SoundType;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;


public class MapScreen extends Screen {

    /** Milliseconds until the screen accepts user input. */
    private static final int SELECTION_TIME = 200;
    private static final Logger logger = Core.getLogger();

    private Cooldown selectionCooldown;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    private ChapterState chapterState;

    public MapScreen(final ChapterState chapterState,final int width, final int height, final int fps) {
        super(width, height, fps);
        SoundPlay.getInstance().play(SoundType.mainGameBgm);
        this.chapterState = chapterState;
        this.returnCode = 1;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        inputDelay = Core.getCooldown(500);
    }

    public final int run() {
        super.run();

        return this.returnCode;
    }
    protected final void update() {
        super.update();
        draw();
        if (this.inputDelay.checkFinished()){
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                sound.SoundPlay.getInstance().play(SoundType.menuSelect);
                chapterState.curPosMove(3);
                this.inputDelay.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                sound.SoundPlay.getInstance().play(SoundType.menuSelect);
                chapterState.curPosMove(2);
                this.inputDelay.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) // 뽑기 버튼으로 가기
                    || inputManager.isKeyDown(KeyEvent.VK_D)) {
                sound.SoundPlay.getInstance().play(SoundType.menuSelect);
                chapterState.curPosMove(0);
                this.inputDelay.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT) // 메뉴 선택으로 되돌아가기
                    || inputManager.isKeyDown(KeyEvent.VK_A)) {
                sound.SoundPlay.getInstance().play(SoundType.menuSelect);
                chapterState.curPosMove(1);
                this.inputDelay.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                sound.SoundPlay.getInstance().play(SoundType.menuClick);
                this.inputDelay.reset();

                int stageCode = chapterState.curStageClear(); // Enemy = 100

                if (stageCode == ChapterState.Stage_Type.ENEMY.ordinal()){
                    sound.SoundPlay.getInstance().stopBgm();
                    returnCode = 100;
                    isRunning = false;
                }
                else if (stageCode == ChapterState.Stage_Type.BOSS.ordinal()) {
                    sound.SoundPlay.getInstance().stopBgm();
                    returnCode = 200;
                    isRunning = false;
                }
            }
        }
    }

    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawLives(this, chapterState.getC_state(C_State.livesRemaining));
        drawManager.drawScore(this, chapterState.getC_state(C_State.score));
        drawManager.drawCoin(this, chapterState.getC_state(C_State.coin));
        drawManager.drawChapters(this, chapterState.getC_state(C_State.chapter));
        drawManager.drawHorizontalLine(this, 50);
        drawManager.drawMap(this, chapterState);

        drawManager.completeDrawing(this);
    }
}
