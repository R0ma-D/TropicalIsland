/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tropicalisland;

import java.util.ArrayList;

/** 
 Ячейка острова
*/
public final class Cell
{
    private static int _id = 0;
    private ArrayList<Cell> neighbors;

    public Cell(int coordinateX, int coordinateY, int islandHeight)
    {
        id = ++_id;
        x = coordinateX;
        y = coordinateY;
        height = islandHeight;
        totalHeight = islandHeight;
        state = State.None;
    }

    /** 
     Координата X
    */
    public final int x;
    
    /** 
     Координата Y
    */
    public final int y;
    
    /** 
     Высота острова в ячейке
    */
    public final int height;
    
    /** 
     Общая высота вместе с уровнем воды
    */
    public int totalHeight;
    
    /** 
     Соседние ячейки
    */
    public final ArrayList<Cell> getNeighbors()
    {
        return neighbors;
    }
    public final void setNeighbors(ArrayList<Cell> value)
    {
        neighbors = value;
    }

    /** 
     Состояние
    */
    public State state;

    /** 
     Идентификатор
    */
    public final int id;
    
    @Override
    public String toString()
    {
        return String.format("%1$s (%2$s,%3$s)", height, x, y);
    }
}
