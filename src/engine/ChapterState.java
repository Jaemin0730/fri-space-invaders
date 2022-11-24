package engine;

import java.util.*;
import java.util.logging.Logger;

/**
 * Implements an object that stores the state of the game between levels.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */

public class ChapterState {
	/** Current map size.*/
	private int map_size;

	private LinkedHashMap<C_State, Integer> c_state;

	public enum Stage_Type {
		NONE,
		ENEMY,
		STORE,
		REPAIR,
		CLEAR,
		BOSS
	}

	public int map_type[][];
	public int is_adj[][];
	public int map_moveable[][][];
	public int map_difficulty[][];
	private int dx[] = {1,-1,0,0};
	private int dy[] = {0,0,1,-1};
	private int cur_x = 0; // <= initialization to start x
	private int cur_y = 0; // <= initialization to start y

	private static final Logger logger = Core.getLogger();


	/**
	 * Constructor.
	 *
	 * @param map_size
	 *            Current map size.
	 */

	public ChapterState(final int map_size) {
		this.map_size = map_size;

		initialize_map(); // 맵 생성 후 저장

		c_state = new LinkedHashMap<>();
		c_state.put(C_State.chapter, 1);
		c_state.put(C_State.difficulty, map_difficulty[cur_y][cur_x]);
		c_state.put(C_State.score, 0);
		c_state.put(C_State.coin, 0);
		c_state.put(C_State.livesRemaining, 3);
		c_state.put(C_State.bulletsShot, 0);
		c_state.put(C_State.shipsDestroyed, 0);
	}

	private void initialize_map(){
		map_type = new int[map_size][map_size];
		is_adj = new int[map_size][map_size];
		map_moveable = new int[map_size][map_size][4];
		map_difficulty = new int[map_size][map_size];

		Random rd = new Random();
		do {
			set_map_maptype(rd);
			set_map_movable();

		} while(bfs(cur_x, cur_y) == 1);
		is_adj[cur_y][cur_x] = 1;
		curStageClear();
		logger.info("map initialized");
	}

	private int bfs(int x, int y){
		class Pair{
			Integer x;
			Integer y;

			public Pair(Integer x, Integer y) {
				this.x = x;
				this.y = y;
			}
		}
		Queue<Pair> q = new LinkedList<>();
		int visit[][] = new int[map_size][map_size];
		if (map_type[y][x] > 0) {
			q.add(new Pair(x, y));
			visit[y][x] = 1;
		}
		ArrayList<Pair> maybeBoss = new ArrayList<>();
		int maxRange = 0;
		while (!q.isEmpty()) {
			Pair p = q.poll();
			if (maxRange < visit[p.y][p.x]){
				maxRange = visit[p.y][p.x];
				maybeBoss.clear();
			}
			maybeBoss.add(p);
			for (int i = 0; i < 4; i++){
				if (map_moveable[p.y][p.x][i] == 1 && visit[p.y + dy[i]][p.x + dx[i]] == 0){
					q.add(new Pair(p.x + dx[i], p.y + dy[i]));
					visit[p.y + dy[i]][p.x + dx[i]] = visit[p.y][p.x] + 1;
				}
			}
		}
		for (int i = 0; i < map_size; i++)
			for (int j = 0; j < map_size; j++)
				if (visit[i][j] == 0 && map_type[i][j] > 0)
					return 1;

		Random rd = new Random();
		Pair bossPos = maybeBoss.get(rd.nextInt(maybeBoss.size()));
		map_type[bossPos.y][bossPos.x] = Stage_Type.values().length - 1;

		return 0;
	}
	public LinkedHashMap getC_state() {
		return c_state;
	}
	public void setC_state(LinkedHashMap c_state) {
		this.c_state = c_state;
	}
	public int getC_state(C_State key) {
		return c_state.get(key);
	}

	public void gainC_state(C_State key, int value) {
		c_state.replace(key, getC_state(key) + value);
	}

	public void setC_state(C_State key, int value){
		c_state.replace(key, value);
	}

	public int curStageClear(){
		for (int i = 0; i < 4; i++)
			if (map_moveable[cur_y][cur_x][i] == 1)
				is_adj[cur_y + dy[i]][cur_x + dx[i]] = 1;

		int tmp = map_type[cur_y][cur_x];
		map_type[cur_y][cur_x] = Stage_Type.CLEAR.ordinal();
		return tmp;
	}

	public void curPosMove(int dir){
		if (map_moveable[cur_y][cur_x][dir] == 1 && is_adj[cur_y+dy[dir]][cur_x+dx[dir]] == 1){
			cur_x += dx[dir];
			cur_y += dy[dir];
			c_state.replace(C_State.difficulty, map_difficulty[cur_y][cur_x]);
		}
	}

	public boolean isCur(int y, int x){
		return x == cur_x && y == cur_y;
	}

	public void set_map_maptype(Random rd){
		for (int i = 0; i < map_size; i++){
			for (int j = 0; j < map_size; j++){
				map_type[i][j] = rd.nextInt(Stage_Type.values().length - 2); // Exclude type { CLEAR, BOSS }
				if (map_type[i][j] == Stage_Type.ENEMY.ordinal())
					map_difficulty[i][j] = rd.nextInt(7);
			}
		}
	}

	public void set_map_movable(){
		for (int i = 0; i < map_size; i++){
			for (int j = 0; j < map_size; j++){
				for (int k = 0; k < 4; k++){
					int cx = j + dx[k], cy = i + dy[k];
					if(cx < 0 || cx >= map_size || cy < 0 || cy >= map_size || map_type[i][j] == 0 || map_type[cy][cx] == 0)
						map_moveable[i][j][k] = 0;
					else
						map_moveable[i][j][k] = 1;
				}
			}
		}
	}
}
