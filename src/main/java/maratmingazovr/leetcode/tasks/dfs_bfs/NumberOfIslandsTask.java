package maratmingazovr.leetcode.tasks.dfs_bfs;

// https://leetcode.com/problems/number-of-islands/description/
public class NumberOfIslandsTask {

    public int numIslands(char[][] grid) {
        if (grid.length == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    clear(i,j, grid);
                }
            }
        }
        return count;
    }

    public void clear(int i, int j, char[][]grid) {

        if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || grid[i][j] == '0') {
            return;
        }

        grid[i][j] = '0';
        clear(i-1, j, grid);
        clear(i+1, j, grid);
        clear(i, j-1, grid);
        clear(i, j+1, grid);
    }
}
