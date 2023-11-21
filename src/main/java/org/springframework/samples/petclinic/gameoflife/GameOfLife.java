package org.springframework.samples.petclinic.gameoflife;

public class GameOfLife {
    public int rows;
    public int columns;

    public int[][] planet;

    public GameOfLife(int rows, int columns,int initialPopulation){
        this.rows=rows;
        this.columns=columns;
        initializePlanet(initialPopulation);
    }

    private void initializePlanet(int initialPopulation) {
        planet=new int[rows][columns];
        if(initialPopulation>=rows*columns)
            initialPopulation=rows;
        for(int i=0;i<initialPopulation;i++){
            int y=(int)(Math.random()*rows);
            int x=(int)(Math.random()*columns);
            if(planet[x][y]==0)
                planet[x][y]=1;
            else
                i--;
        }
    }

    public void evolve(){
        this.planet=nextGeneration(planet);
    }

    protected int[][] nextGeneration(int planet[][])
    {
        int M=planet.length;
        int N=planet[0].length;
        int[][] future = new int[M][N];
 
        // Loop through every cell
        for (int l = 0; l < M; l++)
        {
            for (int m = 0; m < N; m++)
            {
                // finding no Of Neighbours that are alive
                int aliveNeighbours = 0;
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                      if ((l+i>=0 && l+i<M) && (m+j>=0 && m+j<N))
                        aliveNeighbours += planet[l + i][m + j];
 
                // The cell needs to be subtracted from
                // its neighbours as it was counted before
                aliveNeighbours -= planet[l][m];
 
                // Implementing the Rules of Life
 
                // Cell is lonely and dies
                if ((planet[l][m] == 1) && (aliveNeighbours < 2))
                    future[l][m] = 0;
 
                // Cell dies due to over population
                else if ((planet[l][m] == 1) && (aliveNeighbours > 3))
                    future[l][m] = 0;
 
                // A new cell is born
                else if ((planet[l][m] == 0) && (aliveNeighbours == 3))
                    future[l][m] = 1;
 
                // Remains the same
                else
                    future[l][m] = planet[l][m];
            }
        }
 
        return future;
    }

    public String toString(){
        StringBuffer result = new StringBuffer();
        for(int[] row:planet){
            for(int value:row){
                if(value==1)
                    result.append("■");
                else
                    result.append("□");
            }
            result.append("\r\n");
        }
        return result.toString();
    }

    public int getPopulation(){
        int result=0;
        for(int[] row:planet)
            for(int value:row)
                result+=value;
        return result;
    }

}
