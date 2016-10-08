/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tropicalisland;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Roman
 */
public class TropicalIsland {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Считываем входные данные
        ArrayList<int[][]> islandMaps = getIslandMaps();
        
        //Последовательно вычисляем результат
        for (int[][] map : islandMaps)
        {
            Island island = new Island(map);
            int waterAmount = island.CalculateRainfall();
            System.out.println(waterAmount);
        }
    }
    
    private static ArrayList<int[][]> getIslandMaps()
    {
        Scanner scanner = new Scanner(System.in);
        
        // Надеюсь, правильно понял, что не нужно выводить никаких
        // подсказок, проверять входные аргументы и т.п... Просто
        // считываем входной текст в заданном формате
        int islandsCount = scanner.nextInt();
        
        ArrayList<int[][]> islandMaps = new ArrayList<>(islandsCount);
        
        for (int i = 0; i < islandsCount; i++)
        {
            int rows = scanner.nextInt();
            int columns = scanner.nextInt();
            int[][] islandMap = new int[rows][columns];
            
            for (int row = 0; row < rows; row++)
            {
                for (int column = 0; column < columns; column++)
                {
                    islandMap[row][column] = scanner.nextInt();
                }
            }
            
            islandMaps.add(islandMap);
        }
        
        return islandMaps;
    }
}
