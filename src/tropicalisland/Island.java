/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tropicalisland;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Roman
 */
public final class Island {
    
    private final int sizeX;    //Размер острова по X 
    private final int sizeY;    //Размер острова по Y 
    private Cell[][] map;       //Карта осторова
    private int minHeight;      //Минимальная высота острова
    private int maxHeight;      //Максимальная высота острова
    private HashMap<Integer, ArrayList<Cell>> heightMap;    //Карта высот

    public Island(int[][] islandMap)
    {
        sizeX = islandMap.length;
        sizeY = islandMap[0].length;
        Init(islandMap);
    }

    private void Init(int[][] islandMap)
    {
        map = new Cell[sizeX][sizeY];
        if (sizeX <= 2 || sizeY <= 2)
            return;
        
        heightMap = new HashMap<>(Math.max(sizeX, sizeY));
        minHeight = maxHeight = islandMap[0][0];

        Cell cell;
        for (int x = 0; x < sizeX; x++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                cell = new Cell(x, y, islandMap[x][y]);
                map[x][y] = cell;
                
                if (cell.height > maxHeight)
                    maxHeight = cell.height;
                if (cell.height < minHeight)
                    minHeight = cell.height;

                ArrayList<Cell> list;
                if (!heightMap.containsKey(cell.height))
                {
                    list = new ArrayList<>();
                    heightMap.put(cell.height, list);
                }
                else
                {
                    list = heightMap.get(cell.height);
                }
                list.add(cell);
            }
        }

        for (int x = 0; x < sizeX; x++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                cell = map[x][y];
                cell.setNeighbors(getNeighbors(cell));
            }
        }
    }
    
    public final int CalculateRainfall()
    {
        if (sizeX <= 2 || sizeY <= 2)
            return 0;
        
        //Получаем список граничных ячеек
        ArrayList<Cell> edgeCells = GetEdgeCells();

        //Все граничные ячейки + все соседние ячейки с той же высотой или более 
        //помечаем, как ячейки на пути к морю
        for (Cell edgeCell : edgeCells)
            FindPathToSea(edgeCell);

        //Запускаем дождь :)
        Rain();
        
        return CalculateWater();
    }

    private ArrayList<Cell> getNeighbors(Cell cell)
    {
        ArrayList<Cell> cells = new ArrayList<>(4);
        if (cell.x > 0)
            cells.add(map[cell.x - 1][cell.y]);
        if (cell.x < sizeX - 1)
            cells.add(map[cell.x + 1][cell.y]);
        if (cell.y > 0)
            cells.add(map[cell.x][cell.y - 1]);
        if (cell.y < sizeY - 1)
            cells.add(map[cell.x][cell.y + 1]);
        return cells;
    }

    private ArrayList<Cell> GetEdgeCells()
    {
        ArrayList<Cell> cells = new ArrayList<>((sizeX + sizeY) * 2 - 4);
        for (int x = 0; x < sizeX; x++)
        {
            cells.add(map[x][0]);
            cells.add(map[x][sizeY - 1]);
        }

        for (int y = 1; y < sizeY - 1; y++)
        {
            cells.add(map[0][y]);
            cells.add(map[sizeX - 1][y]);
        }

        return cells;
    }

    private void FindPathToSea(Cell cell)
    {
        cell.state = State.PathToSea;
        for (Cell next : cell.getNeighbors())
        {
            if (next.state == State.None && next.height >= cell.height)
                FindPathToSea(next);
        }
    }

    private void Rain()
    {
        ArrayList<Cell> cells = new ArrayList<>(sizeX * sizeY);

        //Самые высокие ячейки - с них всегда сливается вода
        for (Cell cell : heightMap.get(maxHeight))
        {
            if (cell.state == State.None)
                cell.state = State.PathToSea;
        }

        //Собираем все необработанные ячейки и упорядочиваем их по высоте
        int height = minHeight - 1;
        while (height < maxHeight)
        {
            height++;
            
            if (heightMap.containsKey(height))
            {
                ArrayList<Cell> newCells = heightMap.get(height);
                for (Cell cell : newCells)
                {
                    if (cell.state == State.OnProcess)
                        cell.state = State.None;
                    if (cell.state == State.None)
                        cells.add(cell);
                }
            }

            for (Cell cell : cells)
            {
                if (cell.state != State.None)
                    continue;
                
                //Добавляем 1 уровень дождя
                cell.totalHeight++;
            }

            //Если на следующем уровне нет другой высоты - ничего не вычисляем
            //так как вода не сможет перелиться, при этом текущий уровень новых 
            //высот уже был проверен на предыдущем этапе
            if (!heightMap.containsKey(height + 1))
                continue;

            //Проверяем, нет ли теперь пути к морю
            for (Cell cell : cells)
            {
                if (cell.state != State.None)
                    continue;
                
                //Ищем всех соседей с такой же общей высотой
                ArrayList<Cell> result = GetSameTotalHeight(cell);

                boolean hasPathToSea = false;
                for (Cell foundCell : result)
                {
                    if (foundCell.state == State.PathToSea)
                    {
                        hasPathToSea = true;
                        break;
                    }
                }
                
                if (!hasPathToSea)
                {
                    //Если пути к морю нет
                    //Помечаем ячейки проверенными
                    for (Cell foundCell : result)
                    {
                        if (foundCell.state == State.None)
                            foundCell.state = State.OnProcess;
                    }
                    continue;
                }

                //Если одна из ячеек является путём к морю - значит 
                //все остальные ячейки стали путём к морю
                for (Cell pathToSeaCell : result)
                    pathToSeaCell.state = State.PathToSea;

                for (Cell pathToSeaCell : result)
                    FindPathToSeaAfterRain(pathToSeaCell);
            }

            //Сбрасываем состояние ячеек
            for (Cell cell : cells)
            {
                if (cell.state == State.OnProcess)
                    cell.state = State.None;
            }
        }
    }

    private void FindPathToSeaAfterRain(Cell cell)
    {
        for (Cell next : cell.getNeighbors())
        {
            if (next.state == State.None && next.height >= cell.totalHeight)
            {
                next.state = State.PathToSea;
                FindPathToSeaAfterRain(next);
            }
        }
    }

    private ArrayList<Cell> GetSameTotalHeight(Cell sourceCell)
    {
        ArrayList<Cell> result = new ArrayList<>();
        HashSet<Integer> hash = new HashSet<>();
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(sourceCell);
        do
        {
            ArrayList<Cell> nextList = new ArrayList<>(cells.size() * 4);
            for (Cell cell : cells)
            {
                if (!hash.add(cell.id))
                    continue;

                result.add(cell);
                
                for (Cell next : cell.getNeighbors())
                {
                    if (next.totalHeight == sourceCell.totalHeight)
                        nextList.add(next);
                }
            }
            cells = nextList;
        } while (cells.size() > 0);
        return result;
    }

    private int CalculateWater()
    {
        int sum = 0;
        for (int x = 0; x < sizeX; x++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                Cell cell = map[x][y];
                if (cell.state == State.None)
                    throw new IllegalStateException("Не все ячейки проверены!");

                sum += (cell.totalHeight - cell.height);
            }
        }

        return sum;
    }
}
